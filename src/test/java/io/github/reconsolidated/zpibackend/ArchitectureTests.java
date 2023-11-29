package io.github.reconsolidated.zpibackend;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureTests {

    @Test
    public void domainClassesShouldNotDependOnApplicationOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("io.github.reconsolidated.zpibackend");

        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..application..", "..infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    public void applicationClassesShouldNotDependOnInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("your.base.package");

        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..");

        rule.check(importedClasses);
    }
}
