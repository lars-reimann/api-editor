import {VersionedAnnotationStore} from "./VersionedAnnotationStore";

export interface AnnotationStore extends VersionedAnnotationStore {
    schemaVersion: 1;
    attributes?: {
        [target: string]: AttributeAnnotation;
    };
    boundaries?: {
        [target: string]: BoundaryAnnotation;
    };
    calledAfters?: {
        [target: string]: { [calledAfterName: string]: CalledAfterAnnotation };
    };
    completes?: {
        [target: string]: CompleteAnnotation;
    };
    constants?: {
        [target: string]: ConstantAnnotation;
    };
    descriptions?: {
        [target: string]: DescriptionAnnotation;
    };
    enums?: {
        [target: string]: EnumAnnotation;
    };
    groups?: {
        [target: string]: { [groupName: string]: GroupAnnotation };
    };
    moves?: {
        [target: string]: MoveAnnotation;
    };
    optionals?: {
        [target: string]: OptionalAnnotation;
    };
    pures?: {
        [target: string]: PureAnnotation;
    };
    renamings?: {
        [target: string]: RenameAnnotation;
    };
    requireds?: {
        [target: string]: RequiredAnnotation;
    };
    removes?: {
        [target: string]: RemoveAnnotation;
    };
    todos?: {
        [target: string]: TodoAnnotation;
    };
}

export interface Annotation {
    /**
     * ID of the annotated Python declaration.
     */
    readonly target: string;

    /**
     * Usernames of people who created or changed the annotation.
     */
    readonly authors?: string[];

    /**
     * Usernames of people who marked the annotation as correct.
     */
    readonly reviewers?: string[];

    /**
     * Whether the annotation was deleted. This is used for autogenerated annotations. Others are delete outright.
     */
    readonly isRemoved?: boolean;
}

export interface AttributeAnnotation extends Annotation {
    /**
     * Type of default value
     */
    readonly defaultType: DefaultType;

    /**
     * Default value
     */
    readonly defaultValue: DefaultValue;
}

export type DefaultType = 'string' | 'number' | 'boolean' | 'none';
export type DefaultValue = string | number | boolean | null;

export interface BoundaryAnnotation extends Annotation {
    /**
     * The interval specifying possible numeric values
     */
    readonly interval: Interval;
}

export interface Interval {
    /**
     * Whether the type of the interval is discrete or continuous
     */
    readonly isDiscrete: boolean;

    /**
     * Lower interval limit
     */
    readonly lowerIntervalLimit: number;

    /**
     * Whether the lower interval limit is inclusive or exclusive
     */
    readonly lowerLimitType: ComparisonOperator;

    /**
     * Upper interval limit
     */
    readonly upperIntervalLimit: number;

    /**
     * Whether the upper interval limit is inclusive or exclusive
     */
    readonly upperLimitType: ComparisonOperator;
}

export enum ComparisonOperator {
    LESS_THAN_OR_EQUALS,
    LESS_THAN,
    UNRESTRICTED,
}

export interface CalledAfterAnnotation extends Annotation {
    /**
     * Name of the callable to be called first
     */
    readonly calledAfterName: string;
}

export interface CalledAfterTarget {
    /**
     * ID of the annotated Python declaration.
     */
    readonly target: string;

    /**
     * Name of the callable to be called first
     */
    readonly calledAfterName: string;
}

/**
 * The element is fully annotated and all annotations are checked.
 *
 * **Important:** While this is implemented as an annotation it should **not** be counted in the heat map or the
 * statistics.
 */
export interface CompleteAnnotation extends Annotation {}

export interface ConstantAnnotation extends Annotation {
    /**
     * Type of default value
     */
    readonly defaultType: DefaultType;

    /**
     * Default value
     */
    readonly defaultValue: DefaultValue;
}

export interface DescriptionAnnotation extends Annotation {
    /**
     * Description for the declaration.
     */
    readonly newDescription: string;
}

export interface EnumAnnotation extends Annotation {
    /**
     * Name of the enum class that should be created.
     */
    readonly enumName: string;
    readonly pairs: EnumPair[];
}

export interface EnumPair {
    readonly stringValue: string;
    readonly instanceName: string;
}

export interface GroupAnnotation extends Annotation {
    /**
     * Name of the grouped object
     */
    readonly groupName: string;

    /**
     * Parameters to group
     */
    readonly parameters: string[];
}

export interface GroupTarget {
    /**
     * ID of the annotated Python declaration.
     */
    readonly target: string;

    /**
     * Name of the grouped object
     */
    readonly groupName: string;
}

export interface MoveAnnotation extends Annotation {
    /**
     * Qualified path to the destination
     */
    readonly destination: string;
}

export interface OptionalAnnotation extends Annotation {
    /**
     * Type of default value
     */
    readonly defaultType: DefaultType;

    /**
     * Default value
     */
    readonly defaultValue: DefaultValue;
}

export interface PureAnnotation extends Annotation {}

export interface RenameAnnotation extends Annotation {
    /**
     * New name for the declaration.
     */
    readonly newName: string;
}

export interface RequiredAnnotation extends Annotation {}

export interface RemoveAnnotation extends Annotation {}

export interface TodoAnnotation extends Annotation {
    /**
     * A Todo for the declaration.
     */
    readonly newTodo: string;
}