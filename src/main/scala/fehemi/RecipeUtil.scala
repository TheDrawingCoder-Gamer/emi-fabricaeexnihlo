package net.bulbyvr.fehemi.recipes 

import wraith.fabricaeexnihilo.recipe.util.AbstractIngredient
import dev.emi.emi.api.stack.EmiIngredient
import wraith.fabricaeexnihilo.recipe.util.ItemIngredient
import dev.emi.emi.api.stack.TagEmiIngredient
import dev.emi.emi.api.stack.EmiStack
import wraith.fabricaeexnihilo.recipe.util.BlockIngredient
import wraith.fabricaeexnihilo.recipe.util.FluidIngredient
import wraith.fabricaeexnihilo.recipe.util.EntityTypeIngredient
import dev.emi.emi.api.stack.FluidEmiStack
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import dev.emi.emi.api.stack.EmptyEmiStack
import dev.emi.emi.api.stack.ListEmiIngredient
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import scala.jdk.CollectionConverters._
extension (c: BlockIngredient) 
  def getEmiIngredient() : EmiIngredient =
    if c.isEmpty() then 
      EmptyEmiStack()
    else 
      c.getValue().map[EmiIngredient](EmiStack.of(_), {it => 
        ListEmiIngredient(c.streamEntries().map(EmiStack.of(_)).toList(), 1)
      })
extension (c: ItemIngredient)
  def getEmiIngredient(): EmiIngredient  = 
    
    c.getValue().map[EmiIngredient](EmiStack.of(_), TagEmiIngredient(_, 1)) 
extension (c: FluidIngredient) 
  def getEmiIngredient(): EmiIngredient =
    if c.isEmpty() then 
      EmptyEmiStack()
    else 
      c.getValue().map[EmiIngredient](
        it => FluidEmiStack(FluidVariant.of(it), FluidConstants.BUCKET), 
        ing => ListEmiIngredient(
          (for fluid <- c.streamEntries().toList.asScala yield 
            if fluid.isStill(fluid.getDefaultState()) then 
              Some(FluidEmiStack(FluidVariant.of(fluid), FluidConstants.BUCKET))
            else 
              None
          ).flatten.toList.asJava, 1
        )
      )

extension (c: EntityTypeIngredient) 
  def getEmiIngredient(): EmiIngredient = 
    if c.isEmpty() then 
      EmptyEmiStack() 
    else 
      val list = c.flattenListOfEggStacks(it => EmiStack.of(it))
      list.size() match 
        case 0 => EmptyEmiStack()
        case 1 => list.get(0) 
        case _ => ListEmiIngredient(list, 1)
