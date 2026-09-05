package net.thevpc.samples.petstore.test.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class PyramidArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setUp() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("net.thevpc.samples.petstore");
    }

    @Test
    @DisplayName("Rule 1 (Convergence): WS layer must never depend on service-impl or DAL")
    public void wsLayerShouldOnlyDependOnServiceApi() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..ws.rest..")
                .should().dependOnClassesThat().resideInAnyPackage("..service.impl..", "..dal..");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Rule 2 (Convergence): Service implementation must depend only on DAL API, never on concrete DAL JPA")
    public void serviceImplShouldNotDependOnDalJpa() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..service.impl..")
                .should().dependOnClassesThat().resideInAPackage("..dal.jpa..");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Rule 3 (Apex-to-Apex): Order module must never access Catalog DAL or internal service-impl")
    public void orderShouldOnlyAccessCatalogViaFacade() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("net.thevpc.samples.petstore.modules.order..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "net.thevpc.samples.petstore.modules.catalog.dal..",
                        "net.thevpc.samples.petstore.modules.catalog.service.impl.."
                );
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Rule 4 (Driver Isolation): Business modules must never depend directly on drivers")
    public void businessModulesMustNotDependOnDrivers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("net.thevpc.samples.petstore.modules..")
                .should().dependOnClassesThat().resideInAPackage("net.thevpc.samples.petstore.drivers..");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Rule 5 (Driver Isolation): Drivers must never depend on business modules")
    public void driversMustNotDependOnBusinessModules() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("net.thevpc.samples.petstore.drivers..")
                .should().dependOnClassesThat().resideInAPackage("net.thevpc.samples.petstore.modules..");
        rule.check(importedClasses);
    }
}
