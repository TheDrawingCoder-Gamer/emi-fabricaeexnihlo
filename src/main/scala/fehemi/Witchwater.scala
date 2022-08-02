package net.bulbyvr.fehemi.recipes

import wraith.fabricaeexnihilo.recipe.witchwater.WitchWaterWorldRecipe
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe
import wraith.fabricaeexnihilo.util.ItemUtils
import dev.emi.emi.api.stack.FluidEmiStack
import wraith.fabricaeexnihilo.modules.witchwater.WitchWaterFluid
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import dev.emi.emi.api.stack.EmiStack
import net.minecraft.text.LiteralText
import dev.emi.emi.api.EmiRegistry
import wraith.fabricaeexnihilo.recipe.ModRecipes
import wraith.fabricaeexnihilo.recipe.witchwater.WitchWaterEntityRecipe
import net.minecraft.item.SpawnEggItem
import scala.jdk.CollectionConverters._
import java.text.DecimalFormat
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.block.Blocks
import net.minecraft.text.TranslatableText
object EmiWitchwaterRecipe {
  private def blockBuilder(recipe: WitchWaterWorldRecipe): EmiWorldInteractionRecipe = {
    val builder = EmiWorldInteractionRecipe.builder().id(recipe.getId())
                    .supportsRecipeTree(false)
                    .rightInput(EmiStack.of(WitchWaterFluid.STILL, FluidConstants.BUCKET), true)
                    .leftInput(recipe.getTarget().getEmiIngredient())
    val result = recipe.getResult()
    val totalWeight = List.from(result.getValues().values().asScala).foldLeft(0)(_ + _)
    result.getValues().forEach { (k, v) => 
      val chance : Double = (v / totalWeight) * 100
      builder.output(EmiStack.of(k), _.appendTooltip(LiteralText(s"${DecimalFormat("###.##").format(chance)}%")))
    }
    builder.build()
  }
  private def entityBuilder(recipe: WitchWaterEntityRecipe): EmiWorldInteractionRecipe = {
    val builder = EmiWorldInteractionRecipe.builder().id(recipe.getId())
                    .rightInput(FluidEmiStack(FluidVariant.of(WitchWaterFluid.STILL), FluidConstants.BUCKET), true)
    if recipe.getProfession() != null then 
      builder.leftInput(recipe.getTarget().getEmiIngredient(), it => it.appendTooltip(LiteralText(s"-> ${recipe.getProfession()}")))
    else 
      builder.leftInput(recipe.getTarget().getEmiIngredient())
    val displayStack = recipe.getDisplayStack()
    if displayStack.isEmpty() then 
      builder.output(EmiStack.of(Blocks.BARRIER.asItem()), it => it.appendTooltip(recipe.getResult().getName()))
    else 
      builder.output(EmiStack.of(displayStack))
    builder.build()
  }
  def register(registry: EmiRegistry) = {
    registry.getRecipeManager().listAllOfType(ModRecipes.WITCH_WATER_WORLD).forEach(it => registry.addRecipe(blockBuilder(it)))
    registry.getRecipeManager().listAllOfType(ModRecipes.WITCH_WATER_ENTITY).forEach(it => registry.addRecipe(entityBuilder(it)))
  }
}
