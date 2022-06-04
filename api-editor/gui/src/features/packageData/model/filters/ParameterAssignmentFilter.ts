import AbstractPythonFilter from './AbstractPythonFilter';
import PythonModule from '../PythonModule';
import PythonClass from '../PythonClass';
import PythonFunction from '../PythonFunction';
import { AnnotationsState } from '../../../annotations/annotationSlice';
import { UsageCountStore } from '../../../usages/model/UsageCountStore';
import PythonParameter, { PythonParameterAssignment } from '../PythonParameter';

export default class ParameterAssignmentFilter extends AbstractPythonFilter {
    constructor(readonly assignedBy: PythonParameterAssignment) {
        super();
    }

    shouldKeepModule(_pythonModule: PythonModule, _annotations: AnnotationsState, _usages: UsageCountStore): boolean {
        return false;
    }

    shouldKeepClass(_pythonClass: PythonClass, _annotations: AnnotationsState, _usages: UsageCountStore): boolean {
        return false;
    }

    shouldKeepFunction(
        _pythonFunction: PythonFunction,
        _annotations: AnnotationsState,
        _usages: UsageCountStore,
    ): boolean {
        return false;
    }

    shouldKeepParameter(
        pythonParameter: PythonParameter,
        _annotations: AnnotationsState,
        _usages: UsageCountStore,
    ): boolean {
        if (this.assignedBy === PythonParameterAssignment.IMPLICIT) {
            return !pythonParameter.isExplicitParameter();
        } else if (!pythonParameter.isExplicitParameter()) {
            return false;
        } else {
            return pythonParameter.assignedBy === this.assignedBy;
        }
    }
}