package net.bulbyvr.fehemi.recipes

import dev.emi.emi.api.recipe.EmiRecipeCategory
import wraith.fabricaeexnihilo.recipe.ModRecipes
import dev.emi.emi.api.stack.EmiStack
import wraith.fabricaeexnihilo.util.ItemUtils
import dev.emi.emi.api.recipe.EmiRecipe
import wraith.fabricaeexnihilo.recipe.barrel.CompostRecipe
import net.minecraft.util.Identifier
import wraith.fabricaeexnihilo.FabricaeExNihilo
import java.{util => ju}
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.widget.WidgetHolder
import dev.emi.emi.api.widget.SlotWidget
import dev.emi.emi.api.EmiRegistry
import wraith.fabricaeexnihilo.modules.ModBlocks

object EmiCompostRecipe {
  val margin = 10 
  val slotSize = 18 
  val glyph = FabricaeExNihilo.id("textures/gui/rei/glyphs.png")
  val arrowU = 0 
  val arrowV = 16
  val outputX = margin 
  val outputY = margin 
  val arrowX = outputX + slotSize * 2
  val arrowY = outputY 
  val inputX = arrowX + slotSize * 2 
  val inputY = arrowY
  val width = margin * 2 + slotSize * 5 
  val height = margin * 2 + slotSize 

  val compostCategory = EmiRecipeCategory(ModRecipes.COMPOST.id, EmiStack.of(ItemUtils.getExNihiloItemStack("spruce_barrel")))

  def register(registry: EmiRegistry) = 
    registry.addCategory(compostCategory)
    ModBlocks.BARRELS.forEach((_, it) => registry.addWorkstation(compostCategory, EmiStack.of(it)))
    registry.getRecipeManager().listAllOfType(ModRecipes.COMPOST).forEach( it => registry.addRecipe(EmiCompostRecipe(it)))

}
class EmiCompostRecipe(val recipe: CompostRecipe) extends EmiRecipe {
  override def getCategory(): EmiRecipeCategory = EmiCompostRecipe.compostCategory
  override def getId(): Identifier = recipe.getId() 
  override def getDisplayHeight(): Int = EmiCompostRecipe.height
  override def getDisplayWidth(): Int = EmiCompostRecipe.width 
  override def getInputs(): ju.List[EmiIngredient] = ju.List.of(recipe.getInput().getEmiIngredient())
  override def getOutputs(): ju.List[EmiStack] = ju.List.of(EmiStack.of(recipe.getOutput()))
  override def supportsRecipeTree(): Boolean = false 
  override def addWidgets(widgets: WidgetHolder): Unit =  
    import EmiCompostRecipe.* 
    widgets.add(SlotWidget(getOutputs().get(0), outputX, outputY).recipeContext(this))
    widgets.addTexture(glyph, arrowX, arrowY, 16, 16, arrowU, arrowV)
    widgets.addSlot(getInputs().get(0), inputX, inputY)


}
