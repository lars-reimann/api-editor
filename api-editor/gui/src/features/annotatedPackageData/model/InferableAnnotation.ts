import {
    AttributeAnnotation,
    BoundaryAnnotation,
    CalledAfterAnnotation,
    ComparisonOperator,
    ConstantAnnotation,
    DefaultType,
    DefaultValue,
    EnumAnnotation,
    EnumPair,
    GroupAnnotation,
    MoveAnnotation,
    OptionalAnnotation,
    RenameAnnotation,
} from '../../annotations/annotationSlice';

const dataPathPrefix = 'com.larsreimann.api_editor.model.';

const convertDefaultValue = (type: DefaultType, value: DefaultValue) => {
    switch (type) {
        case 'string':
            return {
                type: `${dataPathPrefix}DefaultString`,
                value,
            };
        case 'boolean':
            return {
                type: `${dataPathPrefix}DefaultBoolean`,
                value,
            };
        case 'number':
            return {
                type: `${dataPathPrefix}DefaultNumber`,
                value,
            };
        case 'none':
            return {
                type: `${dataPathPrefix}DefaultNone`,
            };
    }
};

export class InferableAnnotation {
    type: string;

    constructor(type: string) {
        this.type = type;
    }
}

export class InferableAttributeAnnotation extends InferableAnnotation {
    readonly defaultValue: { type: string; value?: DefaultValue };

    constructor(attributeAnnotation: AttributeAnnotation) {
        super(dataPathPrefix + 'AttributeAnnotation');
        this.defaultValue = convertDefaultValue(
            attributeAnnotation.defaultType,
            attributeAnnotation.defaultValue,
        );
    }
}

export class InferableBoundaryAnnotation extends InferableAnnotation {
    readonly isDiscrete: boolean;
    readonly lowerIntervalLimit: number;
    readonly lowerLimitType: string;
    readonly upperIntervalLimit: number;
    readonly upperLimitType: string;

    constructor(boundaryAnnotation: BoundaryAnnotation) {
        super(dataPathPrefix + 'BoundaryAnnotation');
        this.isDiscrete = boundaryAnnotation.interval.isDiscrete;
        this.lowerIntervalLimit =
            boundaryAnnotation.interval.lowerIntervalLimit;
        this.lowerLimitType =
            ComparisonOperator[boundaryAnnotation.interval.lowerLimitType];
        this.upperIntervalLimit =
            boundaryAnnotation.interval.upperIntervalLimit;
        this.upperLimitType =
            ComparisonOperator[boundaryAnnotation.interval.upperLimitType];
    }
}

export class InferableCalledAfterAnnotation extends InferableAnnotation {
    readonly calledAfterName: string;

    constructor(calledAfterAnnotation: CalledAfterAnnotation) {
        super(dataPathPrefix + 'CalledAfterAnnotation');
        this.calledAfterName = calledAfterAnnotation.calledAfterName;
    }
}

export class InferableConstantAnnotation extends InferableAnnotation {
    readonly defaultValue: { type: string; value?: DefaultValue };

    constructor(constantAnnotation: ConstantAnnotation) {
        super(dataPathPrefix + 'ConstantAnnotation');
        this.defaultValue = convertDefaultValue(
            constantAnnotation.defaultType,
            constantAnnotation.defaultValue,
        );
    }
}

export class InferableGroupAnnotation extends InferableAnnotation {
    readonly groupName: string;
    readonly parameters: string[];

    constructor(groupAnnotation: GroupAnnotation) {
        super(dataPathPrefix + 'GroupAnnotation');
        this.groupName = groupAnnotation.groupName;
        this.parameters = groupAnnotation.parameters;
    }
}

export class InferableEnumAnnotation extends InferableAnnotation {
    readonly enumName: string;
    readonly pairs: EnumPair[];

    constructor(enumAnnotation: EnumAnnotation) {
        super(dataPathPrefix + 'EnumAnnotation');
        this.enumName = enumAnnotation.enumName;
        this.pairs = enumAnnotation.pairs;
    }
}

export class InferableMoveAnnotation extends InferableAnnotation {
    readonly destination: string;

    constructor(moveAnnotation: MoveAnnotation) {
        super(dataPathPrefix + 'MoveAnnotation');
        this.destination = moveAnnotation.destination;
    }
}

export class InferableOptionalAnnotation extends InferableAnnotation {
    readonly defaultValue: { type: string; value?: DefaultValue };

    constructor(optionalAnnotation: OptionalAnnotation) {
        super(dataPathPrefix + 'OptionalAnnotation');
        this.defaultValue = convertDefaultValue(
            optionalAnnotation.defaultType,
            optionalAnnotation.defaultValue,
        );
    }
}

export class InferablePureAnnotation extends InferableAnnotation {
    constructor() {
        super(dataPathPrefix + 'PureAnnotation');
    }
}

export class InferableRenameAnnotation extends InferableAnnotation {
    readonly newName: string;

    constructor(renameAnnotation: RenameAnnotation) {
        super(dataPathPrefix + 'RenameAnnotation');
        this.newName = renameAnnotation.newName;
    }
}

export class InferableRequiredAnnotation extends InferableAnnotation {
    constructor() {
        super(dataPathPrefix + 'RequiredAnnotation');
    }
}

export class InferableUnusedAnnotation extends InferableAnnotation {
    constructor() {
        super(dataPathPrefix + 'UnusedAnnotation');
    }
}