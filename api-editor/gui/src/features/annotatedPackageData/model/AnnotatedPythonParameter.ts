import { PythonParameterAssignment } from '../../packageData/model/PythonParameter';
import { InferableAnnotation } from './InferableAnnotation';
import { Optional } from '../../../common/util/types';

export class AnnotatedPythonParameter {
    readonly name: string;
    readonly qualifiedName: string;
    readonly defaultValue: Optional<string>;
    readonly assignedBy: string;
    readonly isPublic: boolean;
    readonly typeInDocs: string;
    readonly description: string;
    readonly annotations: InferableAnnotation[];

    constructor(
        name: string,
        qualifiedName: string,
        defaultValue: Optional<string> = null,
        assignedBy: PythonParameterAssignment = PythonParameterAssignment.POSITION_OR_NAME,
        isPublic: boolean = false,
        typeInDocs: string = '',
        description: string = '',
        annotations: InferableAnnotation[] = [],
    ) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.defaultValue = defaultValue;
        switch (assignedBy) {
            case PythonParameterAssignment.IMPLICIT:
                this.assignedBy = 'IMPLICIT';
                break;
            case PythonParameterAssignment.NAME_ONLY:
                this.assignedBy = 'NAME_ONLY';
                break;
            case PythonParameterAssignment.POSITION_ONLY:
                this.assignedBy = 'POSITION_ONLY';
                break;
            case PythonParameterAssignment.POSITIONAL_VARARG:
                this.assignedBy = 'POSITION_ONLY';
                break;
            case PythonParameterAssignment.POSITION_OR_NAME:
                this.assignedBy = 'POSITION_OR_NAME';
                break;
            case PythonParameterAssignment.NAMED_VARARG:
                this.assignedBy = 'NAMED_VARARG';
                break;
        }
        this.isPublic = isPublic;
        this.typeInDocs = typeInDocs;
        this.description = description;
        this.annotations = annotations;
    }
}
