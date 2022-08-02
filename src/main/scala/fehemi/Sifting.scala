package net.bulbyvr.fehemi.recipes 

import dev.emi.emi.api.recipe.EmiRecipe
import wraith.fabricaeexnihilo.recipe.SieveRecipe
import dev.emi.emi.api.stack.EmiStack
import scala.collection.{mutable => mu}
import scala.jdk.CollectionConverters._
import wraith.fabricaeexnihilo.recipe.util.ItemIngredient
import wraith.fabricaeexnihilo.recipe.util.FluidIngredient
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.TagEmiIngredient
import dev.emi.emi.api.stack.FluidEmiStack
import dev.emi.emi.api.stack.ListEmiIngredient
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.util.Identifier
import net.minecraft.tag.TagKey
import net.minecraft.fluid.Fluid
import java.{util => ju}
import java.{util => ju}
import scala.collection.mutable.ListBuffer
import wraith.fabricaeexnihilo.util.ItemUtils
import java.{util => ju}
import dev.emi.emi.api.recipe.EmiRecipeCategory
import wraith.fabricaeexnihilo.recipe.ModRecipes
import dev.emi.emi.api.widget.WidgetHolder
import dev.emi.emi.api.widget.Widget
import dev.emi.emi.api.widget.Bounds
import wraith.fabricaeexnihilo.FabricaeExNihilo
import dev.emi.emi.api.widget.SlotWidget
import dev.emi.emi.api.EmiRegistry
import wraith.fabricaeexnihilo.modules.ModBlocks
import java.util.Objects
import java.util.Collections
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.text.LiteralText
import dev.emi.emi.api.stack.EmptyEmiStack
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
object EmiSiftingRecipe {
  val slotSize = 18
  val margin = 6 
  val verticalMargin = 10
  val inputX = margin
  val inputY = margin + 6  
  val meshX = inputX
  val meshY = inputY + slotSize
  val fluidX = inputX 
  val fluidY = meshY + slotSize
  val arrowX = inputX + slotSize 
  val arrowY = meshY
  val outputX = arrowX + slotSize 
  val outputY = inputY 
  val slotsWidth = 9
  val slotsHeight = 3
  val outputWidth = slotSize * slotsWidth 
  val outputHeight = slotSize * slotsHeight
  val maxSlots = slotsWidth * slotsHeight
  val width = slotSize * 2 + outputWidth + margin * 2 
  val height = outputHeight + (verticalMargin * 2)
  val siftingCategory = EmiRecipeCategory(ModRecipes.SIEVE.id, EmiStack.of(ItemUtils.getExNihiloItemStack("oak_sieve")))
  def register(registry: EmiRegistry) : Unit = 
    registry.addCategory(siftingCategory)
    ModBlocks.SIEVES.forEach { (k, v) => 
      registry.addWorkstation(siftingCategory, EmiStack.of(v))
    }
    val map : mu.HashMap[Int, SieveRecipeHolder] = mu.HashMap()
    registry.getRecipeManager().listAllOfType(ModRecipes.SIEVE).forEach { it =>
      val recipes = SieveRecipeHolder.fromRecipe(it)
      for (recipe <- recipes) {
        map.updateWith(recipe.hashCode()){
           case Some(value) => 
             Some(value ++ recipe) 
           case None => 
             Some(recipe)
        } 
      }
    }
    map.values.flatMap(it => it.split(maxSlots)).map(EmiSiftingRecipe.apply(_)).foreach(it =>
      registry.addRecipe(it)
    )
}

case class SieveRecipeHolder protected[recipes] (input : EmiIngredient, fluid: EmiIngredient, mesh: EmiStack, outputs: Map[EmiStack, List[Double]]) {
  def ++(recipe: SieveRecipeHolder): SieveRecipeHolder = 
    this.copy(outputs = mergeMaps(outputs, recipe.outputs))
  def split(maxSize: Int):List[SieveRecipeHolder] = 
    (for (outputs <- this.outputs.grouped(maxSize)) yield {
      this.copy(outputs = outputs)
    }).toList
  override def hashCode(): Int = 
    Objects.hash(input, fluid, mesh)
  def fullHash(): Int = 
    Objects.hash(input, fluid, mesh, outputs)
  val id = Identifier("fehemi", "sieve/" + fullHash())
  private def ingredientPath(ing: EmiIngredient): String = 
    if ing.isEmpty then 
      "none"
    else 
      ing.getEmiStacks().asScala.map { it => it.getId().getPath() }.fold("") { (a: String, b: String) => a + "_" + b }
}
object SieveRecipeHolder {
  def fromRecipe(recipe: SieveRecipe): List[SieveRecipeHolder] =
    
    val input = recipe.getInput().getEmiIngredient()
    val fluid = recipe.getFluid().getEmiIngredient()
    val output = EmiStack.of(recipe.getResult())

    (for ((k, v) <- recipe.getRolls().asScala) yield {
      val mesh = EmiStack.of(ItemUtils.getItem(k))
      SieveRecipeHolder(input, fluid, mesh, Map((output, v.asScala.map(Double2double(_)).toList)))
    }).toList

}
class EmiSiftingRecipe(val recipe: SieveRecipeHolder) extends EmiRecipe {
  override def getDisplayHeight(): Int = EmiSiftingRecipe.height 
  override def getDisplayWidth(): Int = EmiSiftingRecipe.width 
  override def getInputs(): ju.List[EmiIngredient] = ju.List.of(recipe.input)
  override def getOutputs(): ju.List[EmiStack] = recipe.outputs.keySet.toList.asJava 
  override def getCatalysts(): ju.List[EmiIngredient] = 
    if recipe.fluid.isEmpty() then 
      Collections.singletonList(recipe.mesh)
    else 
      ju.List.of(recipe.fluid, recipe.mesh) 
  override def getCategory(): EmiRecipeCategory = EmiSiftingRecipe.siftingCategory
  override def getId(): Identifier = recipe.id 
  override def addWidgets(widgets: WidgetHolder): Unit =
    import EmiSiftingRecipe.*
    widgets.addSlot(recipe.input, inputX, inputY)
    widgets.addSlot(recipe.mesh, meshX, meshY)
    if !recipe.fluid.isEmpty() then 
      widgets.addSlot(recipe.fluid, fluidX, fluidY)
    widgets.addTexture(FabricaeExNihilo.id("textures/gui/rei/glyphs.png"), arrowX, arrowY, 16, 16, 0, 0)
    drawOutputSlots(widgets)
  def drawOutputSlots(widgets: WidgetHolder): Unit = 
    import EmiSiftingRecipe.*
    val outputs : List[EmiStack] = getOutputs().asScala.toList
    var noMoreStacks = false 
    for (i <- 0 until (slotsWidth * slotsHeight)) {
      val x = outputX + (i % slotsWidth) * slotSize 
      val y: Int = outputY + Math.floor(i/slotsWidth).toInt * slotSize 
      if !noMoreStacks && i < outputs.length then 
        val stack = outputs(i)
        val widget = SlotWidget(stack, x, y).recipeContext(this)
        for chance <- recipe.outputs.get(stack).get if chance > 0 do 
          widget.appendTooltip(LiteralText(s"${chance * 100}%").formatted(Formatting.GRAY))
        widgets.add(widget)
      else 
        noMoreStacks = true 
        widgets.addSlot(x, y)
    }
  override def supportsRecipeTree(): Boolean = false 
} 


def mergeManyMaps[K, V](lml: List[Map[K, List[V]]]): Map[K, List[V]] = 
  lml.foldLeft(Map.empty[K, List[V]])(mergeMaps)
private def mergeMaps[K, V](m1: Map[K, List[V]], m2: Map[K, List[V]]) : Map[K, List[V]] = 
  val k1 = m1.keySet 
  val k2 = m2.keySet 

  val both = k1 & k2 
  val take1 = k1 &~ both 
  val take2 = k2 &~ both 

  val bothMap = both.map { k => (k, m1(k) ++ m2(k))}.toMap 
  val take1Map = take1.map { k => (k, m1(k)) }.toMap 
  val take2Map = take2.map { k => (k, m2(k)) }.toMap 

  bothMap ++ take1Map ++ take2Map

