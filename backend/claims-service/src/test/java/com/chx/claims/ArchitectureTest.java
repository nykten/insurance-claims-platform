package com.chx.claims;
import org.junit.jupiter.api.Test; import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses; import com.tngtech.archunit.core.importer.ClassFileImporter;
class ArchitectureTest { @Test void apiDoesNotDependOnInfra(){noClasses().that().resideInAnyPackage("..api..").should().dependOnClassesThat().resideInAnyPackage("..infra..").check(new ClassFileImporter().importPackages("com.chx.claims"));} }
