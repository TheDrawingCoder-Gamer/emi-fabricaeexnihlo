package net.bulbyvr.fehemi.recipes

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import wraith.fabricaeexnihilo.recipe.ModRecipes
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.stack.TagEmiIngredient
import wraith.fabricaeexnihilo.util.ItemUtils
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe
import dev.emi.emi.api.stack.EmiIngredient
import wraith.fabricaeexnihilo.recipe.ModRecipes.ModRecipeType
import wraith.fabricaeexnihilo.recipe.ToolRecipe
import com.mojang.datafixers.{util => mdu}
import net.minecraft.block.Block
import net.minecraft.tag.TagKey
import net.minecraft.util.registry.Registry
import dev.emi.emi.api.stack.ListEmiIngredient
object EmiToolRecipe {
  // val CROOK = EmiRecipeCategory(ModRecipes.CROOK.id, EmiStack.of(ItemUtils.getExNihiloItemStack("wooden_crook")))
  // val CRUSHING = EmiRecipeCategory(ModRecipes.HAMMER.id, EmiStack.of(ItemUtils.getExNihiloItemStack("iron_hammer")))
  
  def register(registry: EmiRegistry): Unit = 
    registerHelper(registry, EmiStack.of(ItemUtils.getExNihiloItemStack("wooden_crook")), ModRecipes.CROOK, false)
    registerHelper(registry, EmiStack.of(ItemUtils.getExNihiloItemStack("iron_hammer")), ModRecipes.HAMMER, true)
  private def registerHelper(registry: EmiRegistry, stack: EmiStack, kind: ModRecipeType[ToolRecipe], supportsTree: Boolean): Unit = 
    registry.getRecipeManager().listAllOfType(kind).forEach(it => 
        val input : EmiIngredient = fixerupperEitherToEither(it.getBlock().getValue()) match 
          case Left(untagged : Block) => 
            EmiStack.of(untagged)
          case Right(_) => 
            ListEmiIngredient(it.getBlock().streamEntries().map(EmiStack.of(_)).toList, 1)   
        val output = EmiStack.of(it.getOutput())
        registry.addRecipe(EmiWorldInteractionRecipe.builder().leftInput(input).rightInput(stack, true).output(output).supportsRecipeTree(supportsTree).build())
        () 
    )
  def fixerupperEitherToEither[L, R](either: mdu.Either[L, R]): Either[L, R] = 
    // either.map((l : L) => Left(l) : Either[L, R], (r: R) => Right(r) : Either[L, R])
    either.map[Either[L, R]](Left(_), Right(_))
}

