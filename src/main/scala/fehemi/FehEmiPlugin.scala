package net.bulbyvr.fehemi 

import net.bulbyvr.fehemi.recipes.* 
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry

object FehEmiPlugin extends EmiPlugin {
  override def register(registry: EmiRegistry): Unit = 
    EmiToolRecipe.register(registry)
    EmiCrucibleRecipe.register(registry)
    EmiSiftingRecipe.register(registry)
    EmiAlchemyRecipe.register(registry)
    EmiCompostRecipe.register(registry)
    EmiFluidOnTopRecipe.register(registry)
    EmiTransmuteRecipe.register(registry)
    EmiLeakingRecipe.register(registry)
    EmiMilkingRecipe.register(registry)
    EmiWitchwaterRecipe.register(registry)
}
