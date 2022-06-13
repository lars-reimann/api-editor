import { AnnotationStore } from '../../annotations/annotationSlice';
import { PythonClass } from '../../packageData/model/PythonClass';
import { PythonFunction } from '../../packageData/model/PythonFunction';
import { PythonModule } from '../../packageData/model/PythonModule';
import { PythonPackage } from '../../packageData/model/PythonPackage';
import { PythonParameter } from '../../packageData/model/PythonParameter';
import { PythonResult } from '../../packageData/model/PythonResult';
import { AnnotatedPythonClass } from './AnnotatedPythonClass';
import { AnnotatedPythonFunction } from './AnnotatedPythonFunction';
import { AnnotatedPythonModule } from './AnnotatedPythonModule';
import { AnnotatedPythonPackage } from './AnnotatedPythonPackage';
import { AnnotatedPythonParameter } from './AnnotatedPythonParameter';
import { AnnotatedPythonResult } from './AnnotatedPythonResult';
import {
    InferableAnnotation,
    InferableAttributeAnnotation,
    InferableBoundaryAnnotation,
    InferableCalledAfterAnnotation,
    InferableConstantAnnotation,
    InferableEnumAnnotation,
    InferableGroupAnnotation,
    InferableMoveAnnotation,
    InferableOptionalAnnotation,
    InferablePureAnnotation,
    InferableRemoveAnnotation,
    InferableRenameAnnotation,
    InferableRequiredAnnotation,
    InferableDescriptionAnnotation,
    InferableTodoAnnotation,
} from './InferableAnnotation';

export class AnnotatedPythonPackageBuilder {
    readonly pythonPackage: PythonPackage;
    readonly annotationStore: AnnotationStore;

    constructor(pythonPackage: PythonPackage, annotationStore: AnnotationStore) {
        this.pythonPackage = pythonPackage;
        this.annotationStore = annotationStore;
    }

    generateAnnotatedPythonPackage(): AnnotatedPythonPackage {
        return new AnnotatedPythonPackage(
            this.pythonPackage.name,
            this.pythonPackage.distribution,
            this.pythonPackage.version,
            this.#buildAnnotatedPythonModules(this.pythonPackage.modules),
            this.#getExistingAnnotations(this.pythonPackage.pathAsString()),
        );
    }

    #buildAnnotatedPythonModules(pythonModules: PythonModule[]): AnnotatedPythonModule[] {
        return pythonModules.map(
            (pythonModule: PythonModule) =>
                new AnnotatedPythonModule(
                    pythonModule.name,
                    pythonModule.imports,
                    pythonModule.fromImports,
                    this.#buildAnnotatedPythonClasses(pythonModule),
                    this.#buildAnnotatedPythonFunctions(pythonModule),
                    this.#getExistingAnnotations(pythonModule.pathAsString()),
                ),
        );
    }

    #buildAnnotatedPythonClasses(pythonModule: PythonModule): AnnotatedPythonClass[] {
        return pythonModule.classes.map(
            (pythonClass: PythonClass) =>
                new AnnotatedPythonClass(
                    pythonClass.name,
                    pythonClass.qualifiedName,
                    pythonClass.decorators,
                    pythonClass.superclasses,
                    this.#buildAnnotatedPythonFunctions(pythonClass),
                    pythonClass.isPublic,
                    pythonClass.description,
                    pythonClass.fullDocstring,
                    this.#getExistingAnnotations(pythonClass.pathAsString()),
                ),
        );
    }

    #buildAnnotatedPythonFunctions(pythonDeclaration: PythonModule | PythonClass): AnnotatedPythonFunction[] {
        if (pythonDeclaration instanceof PythonModule) {
            return pythonDeclaration.functions.map((pythonFunction: PythonFunction) =>
                this.#buildAnnotatedPythonFunction(pythonFunction),
            );
        } else {
            return pythonDeclaration.methods.map((pythonFunction: PythonFunction) =>
                this.#buildAnnotatedPythonFunction(pythonFunction),
            );
        }
    }

    #buildAnnotatedPythonFunction(pythonFunction: PythonFunction): AnnotatedPythonFunction {
        return new AnnotatedPythonFunction(
            pythonFunction.name,
            pythonFunction.qualifiedName,
            pythonFunction.decorators,
            this.#buildAnnotatedPythonParameters(pythonFunction.parameters, pythonFunction),
            this.#buildAnnotatedPythonResults(pythonFunction.results),
            pythonFunction.isPublic,
            pythonFunction.description,
            pythonFunction.fullDocstring,
            this.#getExistingAnnotations(pythonFunction.pathAsString()),
        );
    }

    #buildAnnotatedPythonParameters(
        pythonParameters: PythonParameter[],
        pythonFunction: PythonFunction,
    ): AnnotatedPythonParameter[] {
        return pythonParameters.map(
            (pythonParameter: PythonParameter) =>
                new AnnotatedPythonParameter(
                    pythonParameter.name,
                    `${pythonFunction.qualifiedName}.${pythonParameter.name}`,
                    pythonParameter.defaultValue,
                    pythonParameter.assignedBy,
                    pythonParameter.isPublic,
                    pythonParameter.typeInDocs,
                    pythonParameter.description,
                    this.#getExistingAnnotations(pythonParameter.pathAsString()),
                ),
        );
    }

    #buildAnnotatedPythonResults(pythonResults: PythonResult[]): AnnotatedPythonResult[] {
        return pythonResults.map(
            (pythonResult: PythonResult) =>
                new AnnotatedPythonResult(
                    pythonResult.name,
                    pythonResult.typeInDocs,
                    pythonResult.typeInDocs,
                    pythonResult.description,
                    this.#getExistingAnnotations(pythonResult.pathAsString()),
                ),
        );
    }

    #possibleAnnotations = [
        'Attribute',
        'Boundary',
        'CalledAfters',
        'Constant',
        'Description',
        'Enum',
        'Groups',
        'Move',
        'Optional',
        'Pure',
        'Remove',
        'Rename',
        'Required',
        'Todo'
    ];

    #getExistingAnnotations(target: string): InferableAnnotation[] {
        let targetAnnotations: InferableAnnotation[] = [];
        this.#possibleAnnotations.forEach((annotation) => {
            const returnedAnnotations = this.#returnFormattedAnnotation(target, annotation);
            if (returnedAnnotations) {
                targetAnnotations = targetAnnotations.concat(returnedAnnotations);
            }
        });
        return targetAnnotations;
    }

    #returnFormattedAnnotation(
        target: string,
        annotationType: string,
    ): InferableAnnotation[] | InferableAnnotation | undefined {
        switch (annotationType) {
            case 'Attribute':
                const attributeAnnotation = this.annotationStore.attributes[target];
                if (attributeAnnotation) {
                    return new InferableAttributeAnnotation(attributeAnnotation);
                }
                break;
            case 'Boundary':
                const boundaryAnnotation = this.annotationStore.boundaries[target];
                if (boundaryAnnotation) {
                    return new InferableBoundaryAnnotation(boundaryAnnotation);
                }
                break;
            case 'CalledAfters':
                const calledAfterAnnotations = this.annotationStore.calledAfters[target];
                if (!calledAfterAnnotations) {
                    break;
                }
                return Object.values(calledAfterAnnotations).map(
                    (calledAfterAnnotation) => new InferableCalledAfterAnnotation(calledAfterAnnotation),
                );
            case 'Constant':
                const constantAnnotation = this.annotationStore.constants[target];
                if (constantAnnotation) {
                    return new InferableConstantAnnotation(constantAnnotation);
                }
                break;
            case 'Description':
                const descriptionAnnotation = this.annotationStore.descriptions[target];
                if (descriptionAnnotation) {
                    return new InferableDescriptionAnnotation(descriptionAnnotation);
                }
                break;
            case 'Groups':
                const groupAnnotations = this.annotationStore.groups[target];
                if (!groupAnnotations) {
                    break;
                }
                return Object.values(groupAnnotations).map(
                    (groupAnnotation) => new InferableGroupAnnotation(groupAnnotation),
                );
            case 'Enum':
                const enumAnnotation = this.annotationStore.enums[target];
                if (enumAnnotation) {
                    return new InferableEnumAnnotation(enumAnnotation);
                }
                break;
            case 'Move':
                const moveAnnotation = this.annotationStore.moves[target];
                if (moveAnnotation) {
                    return new InferableMoveAnnotation(moveAnnotation);
                }
                break;
            case 'Optional':
                const optionalAnnotation = this.annotationStore.optionals[target];
                if (optionalAnnotation) {
                    return new InferableOptionalAnnotation(optionalAnnotation);
                }
                break;
            case 'Pure':
                const pureAnnotation = this.annotationStore.pures[target];
                if (pureAnnotation) {
                    return new InferablePureAnnotation();
                }
                break;
            case 'Remove':
                const removeAnnotation = this.annotationStore.removes[target];
                if (removeAnnotation) {
                    return new InferableRemoveAnnotation();
                }
                break;
            case 'Rename':
                const renameAnnotation = this.annotationStore.renamings[target];
                if (renameAnnotation) {
                    return new InferableRenameAnnotation(renameAnnotation);
                }
                break;
            case 'Required':
                const requiredAnnotation = this.annotationStore.requireds[target];
                if (requiredAnnotation) {
                    return new InferableRequiredAnnotation();
                }
                break;
            case 'Todo':
                const todoAnnotation = this.annotationStore.todos[target];
                if (todoAnnotation) {
                    return new InferableTodoAnnotation(todoAnnotation);
                }
                break;
        }
        return undefined;
    }
}
