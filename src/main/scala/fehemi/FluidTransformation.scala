package net.bulbyvr.fehemi.recipes

import wraith.fabricaeexnihilo.FabricaeExNihilo
import dev.emi.emi.api.recipe.EmiRecipeCategory
import wraith.fabricaeexnihilo.recipe.ModRecipes
import wraith.fabricaeexnihilo.util.ItemUtils
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.recipe.EmiRecipe
import net.minecraft.util.Identifier
import wraith.fabricaeexnihilo.recipe.barrel.FluidCombinationRecipe
import wraith.fabricaeexnihilo.recipe.util.BlockIngredient
import dev.emi.emi.api.stack.EmiIngredient
import java.{util => ju}
import java.{util => ju}
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
import wraith.fabricaeexnihilo.recipe.barrel.FluidTransformationRecipe
import dev.emi.emi.api.stack.ListEmiIngredient
import net.bulbyvr.fehemi.recipes._
object EmiTransmuteRecipe {
  val slotSize = 18 
  val margin = 10
  val glyph = FabricaeExNihilo.id("textures/gui/rei/glyphs.png")
  val barrelX = margin 
  val barrelY = margin
  val blockX : Int = barrelX + (slotSize / 2) 
  val blockY = barrelY + slotSize 
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

  val transmuteCategory = EmiRecipeCategory(ModRecipes.FLUID_TRANSFORMATION.id, EmiStack.of(ItemUtils.getExNihiloItemStack("birch_barrel")))
  
  def register(registry: EmiRegistry): Unit = {
    registry.addCategory(transmuteCategory)
    ModBlocks.BARRELS.forEach((_, it) => registry.addWorkstation(transmuteCategory, EmiStack.of(it)))
    registry.getRecipeManager().listAllOfType(ModRecipes.FLUID_TRANSFORMATION).forEach { it => 
      registry.addRecipe(EmiTransmuteRecipe(it))
    }
  }
}

class EmiTransmuteRecipe(val recipe: FluidTransformationRecipe) extends EmiRecipe {
    override def getDisplayHeight(): Int = EmiTransmuteRecipe.height 
    override def getDisplayWidth(): Int = EmiTransmuteRecipe.width 
    override def getCategory(): EmiRecipeCategory = EmiTransmuteRecipe.transmuteCategory
    override def getId(): Identifier = recipe.getId() 
    override def getInputs(): ju.List[EmiIngredient] = ju.List.of(recipe.getContained().getEmiIngredient())
    override def getCatalysts(): ju.List[EmiIngredient] = ju.List.of(recipe.getCatalyst().getEmiIngredient())
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
      import EmiTransmuteRecipe.*
      widgets.addSlot(EmiStack.of(ItemUtils.getExNihiloItemStack("oak_barrel")), barrelX, barrelY)
      widgets.addSlot(getInputs().get(0), fluidX, fluidY)
      widgets.add(SlotWidget(getCatalysts().get(0), blockX, blockY).catalyst(true))
      widgets.addTexture(glyph, arrowX, arrowY, 16, 16, arrowU, arrowV)
      widgets.add(SlotWidget(getOutputs().get(0), outputX, outputY).recipeContext(this))
    

}

