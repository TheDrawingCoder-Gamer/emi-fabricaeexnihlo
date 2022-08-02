package net.bulbyvr.fehemi.recipes

import wraith.fabricaeexnihilo.FabricaeExNihilo
import dev.emi.emi.api.recipe.EmiRecipeCategory
import wraith.fabricaeexnihilo.recipe.ModRecipes
import wraith.fabricaeexnihilo.util.ItemUtils
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.recipe.EmiRecipe
import net.minecraft.util.Identifier
import wraith.fabricaeexnihilo.recipe.barrel.FluidCombinationRecipe
import dev.emi.emi.api.stack.EmiIngredient
import java.{util => ju}
import wraith.fabricaeexnihilo.modules.barrels.modes.ItemMode
import wraith.fabricaeexnihilo.modules.barrels.modes.FluidMode
import dev.emi.emi.api.stack.EmptyEmiStack
import dev.emi.emi.api.stack.FluidEmiStack
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import dev.emi.emi.api.widget.WidgetHolder
import dev.emi.emi.api.widget.SlotWidget
import dev.emi.emi.api.EmiRegistry
import wraith.fabricaeexnihilo.modules.ModBlocks
import wraith.fabricaeexnihilo.recipe.barrel.MilkingRecipe

object EmiMilkingRecipe {
  val slotSize = 18 
  val margin = 10
  val glyph = FabricaeExNihilo.id("textures/gui/rei/glyphs.png")
  val mobY = margin
  val mobX = margin
  val barrelX = margin 
  val barrelY = mobY + slotSize 
  val arrowX = barrelX + slotSize 
  val arrowY = barrelY 
  val arrowU = 0 
  val arrowV = 0
  val outputX = arrowX + slotSize 
  val outputY = arrowY 

  val height = margin * 2 + slotSize * 2
  val width = margin * 2 + slotSize * 4 

  val milkingCategory = EmiRecipeCategory(ModRecipes.MILKING.id, EmiStack.of(ItemUtils.getExNihiloItemStack("warped_barrel")))
  
  def register(registry: EmiRegistry): Unit = {
    registry.addCategory(milkingCategory)
    ModBlocks.BARRELS.forEach((_, it) => registry.addWorkstation(milkingCategory, EmiStack.of(it)))
    registry.getRecipeManager().listAllOfType(ModRecipes.MILKING).forEach { it => 
      registry.addRecipe(EmiMilkingRecipe(it))
    }
  }
}

class EmiMilkingRecipe(val recipe: MilkingRecipe) extends EmiRecipe {
    override def getDisplayHeight(): Int = EmiMilkingRecipe.height 
    override def getDisplayWidth(): Int = EmiMilkingRecipe.width 
    override def getCategory(): EmiRecipeCategory = EmiMilkingRecipe.milkingCategory
    override def getId(): Identifier = recipe.getId() 
    override def getInputs(): ju.List[EmiIngredient] = ju.List.of()
    override def getCatalysts(): ju.List[EmiIngredient] = ju.List.of(recipe.getEntity().getEmiIngredient())
    override def getOutputs(): ju.List[EmiStack] = ju.List.of({ 
      val fluid = recipe.getFluid()
      if fluid == null then 
        EmptyEmiStack() 
      else 
        FluidEmiStack(fluid, recipe.getAmount())
    })
    override def addWidgets(widgets: WidgetHolder): Unit = 
      import EmiMilkingRecipe.*
      widgets.addSlot(EmiStack.of(ItemUtils.getExNihiloItemStack("oak_barrel")), barrelX, barrelY)
      widgets.add(SlotWidget(getCatalysts().get(0), mobX, mobY).catalyst(true))
      widgets.addTexture(glyph, arrowX, arrowY, 16, 16, arrowU, arrowV)
      widgets.add(SlotWidget(getOutputs().get(0), outputX, outputY).recipeContext(this))


}
