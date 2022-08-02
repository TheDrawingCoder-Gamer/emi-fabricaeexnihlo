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
import wraith.fabricaeexnihilo.recipe.util.FluidIngredient
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
import wraith.fabricaeexnihilo.recipe.barrel.LeakingRecipe
object EmiLeakingRecipe {
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

  val leakingCategory = EmiRecipeCategory(ModRecipes.LEAKING.id, EmiStack.of(ItemUtils.getExNihiloItemStack("dark_oak_barrel")))
  
  def register(registry: EmiRegistry): Unit = {
    registry.addCategory(leakingCategory)
    ModBlocks.BARRELS.forEach((_, it) => if !it.isFireproof() then registry.addWorkstation(leakingCategory, EmiStack.of(it)))
    registry.getRecipeManager().listAllOfType(ModRecipes.LEAKING).forEach { it => 
      registry.addRecipe(EmiLeakingRecipe(it))
    }
  }
}

class EmiLeakingRecipe(val recipe: LeakingRecipe) extends EmiRecipe {
    override def getDisplayHeight(): Int = EmiLeakingRecipe.height 
    override def getDisplayWidth(): Int = EmiLeakingRecipe.width 
    override def getCategory(): EmiRecipeCategory = EmiLeakingRecipe.leakingCategory
    override def getId(): Identifier = recipe.getId() 
    override def getInputs(): ju.List[EmiIngredient] = ju.List.of(recipe.getFluid().getFluidIngredient(recipe.getAmount()), recipe.getBlock().getEmiIngredient())
    override def getOutputs(): ju.List[EmiStack] = ju.List.of(EmiStack.of(recipe.getResult()))
    override def addWidgets(widgets: WidgetHolder): Unit = 
      import EmiLeakingRecipe.*
      widgets.addSlot(EmiStack.of(ItemUtils.getExNihiloItemStack("oak_barrel")), barrelX, barrelY)
      widgets.addSlot(getInputs().get(0), fluidX, fluidY)
      widgets.add(SlotWidget(getInputs().get(1), blockX, blockY))
      widgets.addTexture(glyph, arrowX, arrowY, 16, 16, arrowU, arrowV)
      widgets.add(SlotWidget(getOutputs().get(0), outputX, outputY).recipeContext(this))
    

}

extension (c: FluidIngredient) 
  def getFluidIngredient(amount: Long): EmiIngredient = 
    c.getValue().map[EmiIngredient](EmiStack.of(_, amount), { it => 
      if c.isEmpty() then 
        EmptyEmiStack()
      else
        val list = c.streamEntries().filter(f => f.isStill(f.getDefaultState())).map(EmiStack.of(_, amount)).toList()
        list.size() match
          case 0 => EmptyEmiStack()
          case 1 => list.get(0)
          case _ => ListEmiIngredient(list, amount)
      
    })
