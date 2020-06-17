/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.commonizer.mergedtree

import org.jetbrains.kotlin.descriptors.commonizer.cir.CirClass
import org.jetbrains.kotlin.descriptors.commonizer.utils.CommonizedGroup
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.storage.NullableLazyValue

class CirClassNode(
    override val targetDeclarations: CommonizedGroup<CirClass>,
    override val commonDeclaration: NullableLazyValue<CirClass>,
    override val fqName: FqName
) : CirNodeWithFqName<CirClass, CirClass> {

    val constructors: MutableMap<ConstructorApproximationKey, CirClassConstructorNode> = HashMap()
    val properties: MutableMap<PropertyApproximationKey, CirPropertyNode> = HashMap()
    val functions: MutableMap<FunctionApproximationKey, CirFunctionNode> = HashMap()
    val classes: MutableMap<Name, CirClassNode> = HashMap()

    override fun <R, T> accept(visitor: CirNodeVisitor<R, T>, data: T): R =
        visitor.visitClassNode(this, data)

    override fun toString() = CirNode.toString(this)
}