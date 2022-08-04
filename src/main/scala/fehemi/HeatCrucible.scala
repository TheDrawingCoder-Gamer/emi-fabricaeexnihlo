package net.bulbyvr.fehemi.recipes 

import dev.emi.emi.api.recipe.EmiRecipeCategory
import wraith.fabricaeexnihilo.recipe.ModRecipes
import wraith.fabricaeexnihilo.util.ItemUtils
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.widget.WidgetHolder
import net.minecraft.util.Identifier
import wraith.fabricaeexnihilo.recipe.crucible.CrucibleHeatRecipe
import dev.emi.emi.api.stack.EmiIngredient
import java.{util => ju}
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.EmiRegistry
import wraith.fabricaeexnihilo.modules.ModBlocks
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

object EmiHeatCrucibleRecipe {
  val heatCrucibleCategory = EmiRecipeCategory(ModRecipes.CRUCIBLE_HEAT.id, EmiStack.of(ItemUtils.getExNihiloItemStack("crimson_crucible")))
  val margin = 10 
  val slotSize = 18 
  val arrowU = 0
  val arrowV = 0 
  val flameX = margin 
  val flameY = margin + slotSize 
  val textX = flameX + slotSize 
  val textY = flameY
  val fuelX = flameX 
  val fuelY = flameY + slotSize
  val width = 125 
  val height = margin * 2 + slotSize * 3
  def register(registry: EmiRegistry): Unit = 
    registry.addCategory(heatCrucibleCategory)
    ModBlocks.CRUCIBLES.values().forEach(it => if it.isFireproof() then registry.addWorkstation(heatCrucibleCategory, EmiStack.of(it)))
    registry.getRecipeManager().listAllOfType(ModRecipes.CRUCIBLE_HEAT).forEach(it => registry.addRecipe(EmiHeatCrucibleRecipe(it)))
}

class EmiHeatCrucibleRecipe(val recipe: CrucibleHeatRecipe) extends EmiRecipe {
  override def getDisplayHeight(): Int = EmiHeatCrucibleRecipe.height 
  override def getDisplayWidth(): Int = EmiHeatCrucibleRecipe.width 
  override def getId(): Identifier = recipe.getId() 
  override def getCategory(): EmiRecipeCategory = EmiHeatCrucibleRecipe.heatCrucibleCategory 
  override def getInputs(): ju.List[EmiIngredient] = ju.List.of(recipe.getBlock().getEmiIngredient()) 
  override def getOutputs(): ju.List[EmiStack] = ju.List.of()
  override def addWidgets(widgets: WidgetHolder): Unit = 
    import EmiHeatCrucibleRecipe.* 
    widgets.addSlot(recipe.getBlock().getEmiIngredient(), fuelX, fuelY)
    widgets.addTexture(EmiTexture.EMPTY_FLAME, flameX, flameY)
    widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, flameX, flameY, 1000 / recipe.getHeat(), false, true, true)
    widgets.addText(TranslatableText("fabricaeexnihilo.rei.category.crucible_heat.speed", recipe.getHeat()).asOrderedText(), textX, textY, 0xFF404040, false) 
    
}
