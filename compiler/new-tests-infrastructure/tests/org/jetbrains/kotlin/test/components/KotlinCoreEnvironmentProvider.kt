/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test.components

import com.intellij.openapi.Disposable
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.JvmContentRoot
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.test.model.TestFile
import org.jetbrains.kotlin.test.model.TestModule

/*
 * TODO: add components which additionally configure KotlinCoreEnvironment and CompilerConfiguration
 */
abstract class KotlinCoreEnvironmentProvider(
    val components: ConfigurationComponents
) {
    abstract val testRootDisposable: Disposable

    abstract fun getKotlinCoreEnvironment(module: TestModule): KotlinCoreEnvironment
}

class KotlinCoreEnvironmentProviderImpl(
    components: ConfigurationComponents,
    override val testRootDisposable: Disposable
) : KotlinCoreEnvironmentProvider(components) {
    override fun getKotlinCoreEnvironment(module: TestModule): KotlinCoreEnvironment {
        return KotlinCoreEnvironment.createForTests(
            testRootDisposable,
            createCompilerConfiguration(module),
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )
    }

    private fun createCompilerConfiguration(module: TestModule): CompilerConfiguration {
        val configuration = CompilerConfiguration()
        configuration[CommonConfigurationKeys.MODULE_NAME] = module.name
        // TODO: do we need message collector?
        // see org/jetbrains/kotlin/test/KotlinTestUtils.java:304

        // TODO: add parsing flags from directives
        // see org/jetbrains/kotlin/test/KotlinBaseTest.kt:93
        configuration[JVMConfigurationKeys.IR, false]

        module.javaFiles.takeIf { it.isNotEmpty() }?.let { javaFiles ->
            val files = javaFiles.map { components.sourceFileProvider.getRealFileForSourceFile(it) }
            configuration.addJavaSourceRoots(files)
        }

        return configuration
    }

    private operator fun <T : Any> CompilerConfiguration.set(key: CompilerConfigurationKey<T>, value: T) {
        put(key, value)
    }
}

val TestModule.javaFiles: List<TestFile>
    get() = files.filter { it.isJavaFile }
