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

object EmiFluidOnTopRecipe {
  val slotSize = 18 
  val margin = 10
  val glyph = FabricaeExNihilo.id("textures/gui/rei/glyphs.png")
  val catalystY = margin 
  val barrelX = margin 
  val barrelY = catalystY + slotSize 
  val catalystX : Int = barrelX + (slotSize / 2) 
  val fluidX = barrelX + slotSize 
  val fluidY = barrelY 
  val arrowX = fluidX + slotSize 
  val arrowY = fluidY 
  val arrowU = 0 
  val arrowV = 0
  val outputX = arrowX + slotSize 
  val outputY = arrowY 

  val height = margin * 2 + slotSize * 2
  val width = margin * 2 + slotSize * 4 

  val fluidOnTopCategory = EmiRecipeCategory(ModRecipes.FLUID_COMBINATION.id, EmiStack.of(ItemUtils.getExNihiloItemStack("acacia_barrel")))
  
  def register(registry: EmiRegistry): Unit = {
    registry.addCategory(fluidOnTopCategory)
    ModBlocks.BARRELS.forEach((_, it) => registry.addWorkstation(fluidOnTopCategory, EmiStack.of(it)))
    registry.getRecipeManager().listAllOfType(ModRecipes.FLUID_COMBINATION).forEach { it => 
      registry.addRecipe(EmiFluidOnTopRecipe(it))
    }
  }
}

class EmiFluidOnTopRecipe(val recipe: FluidCombinationRecipe) extends EmiRecipe {
    override def getDisplayHeight(): Int = EmiFluidOnTopRecipe.height 
    override def getDisplayWidth(): Int = EmiFluidOnTopRecipe.width 
    override def getCategory(): EmiRecipeCategory = EmiFluidOnTopRecipe.fluidOnTopCategory
    override def getId(): Identifier = recipe.getId() 
    override def getInputs(): ju.List[EmiIngredient] = ju.List.of(recipe.getContained().getEmiIngredient())
    override def getCatalysts(): ju.List[EmiIngredient] = ju.List.of(recipe.getOther().getEmiIngredient())
    override def getOutputs(): ju.List[EmiStack] = ju.List.of({
      recipe.getResult() match 
        case item : ItemMode => 
          EmiStack.of(item.getStack())
        case fluidMode : FluidMode => 
          val fluid = fluidMode.getFluid()
          if fluid == null then 
            EmptyEmiStack()
          else 
            FluidEmiStack(fluid, FluidConstants.BUCKET)
        case _ => 
          EmptyEmiStack()
    })
    override def addWidgets(widgets: WidgetHolder): Unit = 
      import EmiFluidOnTopRecipe.*
      widgets.addSlot(EmiStack.of(ItemUtils.getExNihiloItemStack("oak_barrel")), barrelX, barrelY)
      widgets.addSlot(getInputs().get(0), fluidX, fluidY)
      widgets.add(SlotWidget(getCatalysts().get(0), catalystX, catalystY).catalyst(true))
      widgets.addTexture(glyph, arrowX, arrowY, 16, 16, arrowU, arrowV)
      widgets.add(SlotWidget(getOutputs().get(0), outputX, outputY).recipeContext(this))


}
