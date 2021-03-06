import { PythonClass } from '../../packageData/model/PythonClass';
import { PythonFunction } from '../../packageData/model/PythonFunction';
import { PythonModule } from '../../packageData/model/PythonModule';
import { PythonParameter } from '../../packageData/model/PythonParameter';
import { PythonPackage } from '../../packageData/model/PythonPackage';
import { isEmptyList } from '../../../common/util/listOperations';
import { PythonDeclaration } from '../../packageData/model/PythonDeclaration';
import { AnnotationStore } from '../../annotations/versioning/AnnotationStoreV2';
import { UsageCountStore } from '../../usages/model/UsageCountStore';

/**
 * An abstract base class for filters of Python declarations. To create a new filter create a new subclass and override
 * the abstract shouldKeepXXX methods.
 */
export abstract class AbstractPythonFilter {
    /**
     * Whether the given module should be kept after filtering.
     */
    abstract shouldKeepModule(
        pythonModule: PythonModule,
        annotations: AnnotationStore,
        usages: UsageCountStore,
    ): boolean;

    /**
     * Whether the given class should be kept after filtering.
     */
    abstract shouldKeepClass(pythonClass: PythonClass, annotations: AnnotationStore, usages: UsageCountStore): boolean;

    /**
     * Whether the given function should be kept after filtering.
     */
    abstract shouldKeepFunction(
        pythonFunction: PythonFunction,
        annotations: AnnotationStore,
        usages: UsageCountStore,
    ): boolean;

    /**
     * Whether the given parameter should be kept after filtering.
     */
    abstract shouldKeepParameter(
        pythonParameter: PythonParameter,
        annotations: AnnotationStore,
        usages: UsageCountStore,
    ): boolean;

    /**
     * Whether the given declaration should be kept after filtering. This function generally does not need to be
     * overridden.
     */
    shouldKeepDeclaration(
        pythonDeclaration: PythonDeclaration,
        annotations: AnnotationStore,
        usages: UsageCountStore,
    ): boolean {
        if (pythonDeclaration instanceof PythonModule) {
            return this.shouldKeepModule(pythonDeclaration, annotations, usages);
        } else if (pythonDeclaration instanceof PythonClass) {
            return this.shouldKeepClass(pythonDeclaration, annotations, usages);
        } else if (pythonDeclaration instanceof PythonFunction) {
            return this.shouldKeepFunction(pythonDeclaration, annotations, usages);
        } else if (pythonDeclaration instanceof PythonParameter) {
            return this.shouldKeepParameter(pythonDeclaration, annotations, usages);
        } else {
            return true;
        }
    }

    /**
     * Applies this filter to the given package and creates a package with filtered modules. This function should not be
     * overridden.
     */
    applyToPackage(pythonPackage: PythonPackage, annotations: AnnotationStore, usages: UsageCountStore): PythonPackage {
        // Filter modules
        const modules = pythonPackage.modules
            .map((it) => this.applyToModule(it, annotations, usages))
            .filter((it) => it !== null);

        // Create filtered package
        return pythonPackage.shallowCopy({
            modules: modules as PythonModule[],
        });
    }

    /**
     * Applies this filter to the given module and creates a module with filtered classes and functions. Returns null if
     * the module should be removed.
     */
    private applyToModule(
        pythonModule: PythonModule,
        annotations: AnnotationStore,
        usages: UsageCountStore,
    ): PythonModule | null {
        // Filter classes
        const classes = pythonModule.classes
            .map((it) => this.applyToClass(it, annotations, usages))
            .filter((it) => it !== null);

        // Filter functions
        const functions = pythonModule.functions
            .map((it) => this.applyToFunction(it, annotations, usages))
            .filter((it) => it !== null);

        // Return null if all classes and functions are removed
        if (
            !this.shouldKeepModule(pythonModule, annotations, usages) &&
            isEmptyList(classes) &&
            isEmptyList(functions)
        ) {
            return null;
        }

        // Otherwise, create filtered module
        return pythonModule.shallowCopy({
            classes: classes as PythonClass[],
            functions: functions as PythonFunction[],
        });
    }

    /**
     * Applies this filter to the given class and creates a class with filtered methods. Returns null if the class
     * should be removed.
     */
    private applyToClass(
        pythonClass: PythonClass,
        annotations: AnnotationStore,
        usages: UsageCountStore,
    ): PythonClass | null {
        // Filter methods
        const methods = pythonClass.methods
            .map((it) => this.applyToFunction(it, annotations, usages))
            .filter((it) => it !== null);

        // Return null if all methods are removed
        if (!this.shouldKeepClass(pythonClass, annotations, usages) && isEmptyList(methods)) {
            return null;
        }

        // Otherwise, create filtered class
        return pythonClass.shallowCopy({
            methods: methods as PythonFunction[],
        });
    }

    /**
     * Applies this filter to the given function and creates a function with filtered parameters. Returns null if the
     * function should be removed.
     */
    private applyToFunction(
        pythonFunction: PythonFunction,
        annotations: AnnotationStore,
        usages: UsageCountStore,
    ): PythonFunction | null {
        // Filter parameters
        const parameters = pythonFunction.parameters.filter((it) => this.shouldKeepParameter(it, annotations, usages));

        // Return null if all parameters are removed
        if (!this.shouldKeepFunction(pythonFunction, annotations, usages) && isEmptyList(parameters)) {
            return null;
        }

        // Otherwise, create filtered function
        return pythonFunction.shallowCopy({
            parameters,
        });
    }
}
