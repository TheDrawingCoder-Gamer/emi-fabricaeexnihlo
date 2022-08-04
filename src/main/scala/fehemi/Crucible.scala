package net.bulbyvr.fehemi.recipes

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.stack.FluidEmiStack
import dev.emi.emi.api.stack.EmiStack
import wraith.fabricaeexnihilo.FabricaeExNihilo
import wraith.fabricaeexnihilo.recipe.crucible.CrucibleRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.TagEmiIngredient
import dev.emi.emi.api.stack.EmiIngredient
import java.util.Collections
import java.{util => ju}
import dev.emi.emi.api.widget.WidgetHolder
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.widget.SlotWidget
import dev.emi.emi.api.EmiRegistry
import wraith.fabricaeexnihilo.recipe.ModRecipes
import wraith.fabricaeexnihilo.util.ItemUtils
import wraith.fabricaeexnihilo.modules.ModBlocks

object CrucibleMeasures {
  val arrow = FabricaeExNihilo.id("textures/gui/rei/glyphs.png")
  val margin = 6 
  val height = margin * 2 + 16
  val inputX = margin
  val arrowX = inputX + 18 
  val outputX = arrowX + 18
  val inputY = margin 
  val arrowY = margin 
  val outputY = margin 
  val width = margin * 2 + 18 * 6 


}
class EmiCrucibleRecipe(val recipe: CrucibleRecipe, val category: EmiRecipeCategory) extends EmiRecipe {
    override def getCategory(): EmiRecipeCategory = category
    override def getDisplayHeight(): Int = CrucibleMeasures.height 
    override def getDisplayWidth(): Int = CrucibleMeasures.width
    override def getId() = recipe.getId()
    override def getInputs() = Collections.singletonList(getInput())
    def getInput(): EmiIngredient = 
      recipe.getInput().getValue().map[EmiIngredient](EmiStack.of(_), EmiIngredient.of(_))
    def getFluid(): EmiStack = 
      val fluid = recipe.getFluid()
      FluidEmiStack(fluid).setAmount(recipe.getAmount())
    override def getOutputs(): ju.List[EmiStack] = Collections.singletonList(getFluid())
    override def addWidgets(widgets: WidgetHolder): Unit =
      import CrucibleMeasures.*
      widgets.addSlot(getInput(), inputX, CrucibleMeasures.inputY)
      widgets.addTexture(EmiTexture(arrow, 0, 0, 16, 16), arrowX, arrowY)
      widgets.add(SlotWidget(getFluid(), outputX, outputY).recipeContext(this))
      // no amount needed, it's in the tooltip
}

object EmiCrucibleRecipe {
  val crucibleCategory = EmiRecipeCategory(ModRecipes.CRUCIBLE.id, EmiStack.of(ItemUtils.getExNihiloItemStack("oak_crucible")))
  val fireproofCrucibleCategory = EmiRecipeCategory(FabricaeExNihilo.id("fireproof_crucible"), EmiStack.of(ItemUtils.getExNihiloItemStack("porcelain_crucible")))
  def register(registry:EmiRegistry): Unit =
    registry.addCategory(crucibleCategory)
    registry.addCategory(fireproofCrucibleCategory)
    ModBlocks.CRUCIBLES.forEach((_, it) => {
      registry.addWorkstation(crucibleCategory,  EmiStack.of(it))
      if (it.isFireproof()) {
        registry.addWorkstation(fireproofCrucibleCategory, EmiStack.of(it))
      }
    })
    registry.getRecipeManager().listAllOfType(ModRecipes.CRUCIBLE).forEach(it => 
      if it.requiresFireproofCrucible() then 
        registry.addRecipe(EmiCrucibleRecipe(it, fireproofCrucibleCategory))
      else 
        registry.addRecipe(EmiCrucibleRecipe(it, crucibleCategory))
    )


}
