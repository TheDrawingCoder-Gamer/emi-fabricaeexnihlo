package net.bulbyvr.fehemi.recipes 

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import wraith.fabricaeexnihilo.recipe.ModRecipes
import wraith.fabricaeexnihilo.util.ItemUtils
import dev.emi.emi.api.stack.EmiStack
import wraith.fabricaeexnihilo.recipe.barrel.AlchemyRecipe
import java.{util => ju}
import dev.emi.emi.api.stack.EmiIngredient
import java.{util => ju}
import wraith.fabricaeexnihilo.modules.barrels.modes.ItemMode
import dev.emi.emi.api.stack.FluidEmiStack
import wraith.fabricaeexnihilo.modules.barrels.modes.FluidMode
import dev.emi.emi.api.stack.EmptyEmiStack
import net.minecraft.item.SpawnEggItem
import dev.emi.emi.api.widget.WidgetHolder
import wraith.fabricaeexnihilo.FabricaeExNihilo
import dev.emi.emi.api.widget.SlotWidget
import dev.emi.emi.api.EmiRegistry
import wraith.fabricaeexnihilo.modules.ModBlocks
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants

object EmiAlchemyRecipe {
  val glyph = FabricaeExNihilo.id("textures/gui/rei/glyphs.png")
  val margin = 6 
  val slotSize = 18
  val inputX = margin 
  val inputY = margin 
  val plusU = 3 * 16
  val plusV = 0 
  val plusX = inputX + slotSize 
  val plusY = inputY 
  val fluidX = plusX + slotSize 
  val fluidY = inputY 
  val arrowU = 0
  val arrowV = 0
  val arrowX = fluidX + slotSize 
  val arrowY = fluidY

  // why does there need to be a barrel in the recipe 
  // i'm omitting it 

  val productX = arrowX + slotSize
  val productY = arrowY 
  val byproductX = productX + slotSize 
  val byproductY = productY 
  val toSpawnX = byproductX + slotSize 
  val toSpawnY = byproductY

  val height = margin * 2 + slotSize 
  val width = margin * 2 + slotSize * 7

  val alchemyCategory = EmiRecipeCategory(ModRecipes.ALCHEMY.id, EmiStack.of(ItemUtils.getExNihiloItemStack("oak_barrel")))

  def register(registry: EmiRegistry): Unit = 
    registry.addCategory(alchemyCategory)
    ModBlocks.BARRELS.forEach { (_, it) => 
      registry.addWorkstation(alchemyCategory, EmiStack.of(it))
    }
    registry.getRecipeManager().listAllOfType(ModRecipes.ALCHEMY).forEach( it => registry.addRecipe(EmiAlchemyRecipe(it)))
}
class EmiAlchemyRecipe(val recipe: AlchemyRecipe) extends EmiRecipe {
  override def getDisplayHeight(): Int = EmiAlchemyRecipe.height 
  override def getDisplayWidth(): Int = EmiAlchemyRecipe.width 
  override def getId() = recipe.getId() 
  override def getCategory(): EmiRecipeCategory = EmiAlchemyRecipe.alchemyCategory
  override def getInputs(): ju.List[EmiIngredient] = ju.List.of(recipe.getReactant().getEmiIngredient(), recipe.getCatalyst().getEmiIngredient()) 
  override def getOutputs(): ju.List[EmiStack] = ju.List.of({
    recipe.getResult() match 
      case itemMode : ItemMode => 
        EmiStack.of(itemMode.getStack())
      case fluidMode : FluidMode => 
        val fluid = fluidMode.getFluid().getFluid()
        if fluid != null then 
          FluidEmiStack(FluidVariant.of(fluid), FluidConstants.BUCKET)
        else 
          EmptyEmiStack() 
      case _ => EmptyEmiStack()
  }, EmiStack.of(recipe.getByproduct().stack()), 
  if !recipe.getToSpawn().isEmpty() then 
    EmiStack.of(SpawnEggItem.forEntity(recipe.getToSpawn().getType()))
  else 
    EmptyEmiStack()
  )
  override def addWidgets(widgets: WidgetHolder): Unit =  
    import EmiAlchemyRecipe.* 
    val outputs = getOutputs()
  
    widgets.addSlot(recipe.getCatalyst().getEmiIngredient(), inputX, inputY)
    widgets.addTexture(glyph, plusX, plusY, 16, 16, plusU, plusV)
    widgets.addSlot(recipe.getReactant().getEmiIngredient(), fluidX, fluidY)
    widgets.addTexture(glyph, arrowX, arrowY, 16, 16, arrowU, arrowV)
    val product = outputs.get(0) 
    val byproduct = outputs.get(1)
    val toSpawn = outputs.get(2) 
    widgets.add(SlotWidget(product, productX, productY).recipeContext(this)) 
    widgets.add(SlotWidget(byproduct, byproductX, byproductY).recipeContext(this)) 
    widgets.add(SlotWidget(toSpawn, toSpawnX, toSpawnY).recipeContext(this)) 
}
