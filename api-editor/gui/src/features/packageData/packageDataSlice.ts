import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '../../app/store';

export interface PackageDataState {
    expandedInTreeView: {
        [target: string]: true;
    };
    treeViewScrollOffset: number;
    showImportDialog: boolean;
    showPrivateDeclarations: boolean;
}

// Initial state -------------------------------------------------------------------------------------------------------

const initialState: PackageDataState = {
    expandedInTreeView: {},
    treeViewScrollOffset: 0,
    showImportDialog: false,
    showPrivateDeclarations: false,
};

// Slice ---------------------------------------------------------------------------------------------------------------

const packageDataSlice = createSlice({
    name: 'packageData',
    initialState,
    reducers: {
        toggleIsExpanded(state, action: PayloadAction<string>) {
            if (state.expandedInTreeView[action.payload]) {
                delete state.expandedInTreeView[action.payload];
            } else {
                state.expandedInTreeView[action.payload] = true;
            }
        },
        setScrollOffset(state, action: PayloadAction<number>) {
            state.treeViewScrollOffset = action.payload;
        },
        toggleImportDialog(state) {
            state.showImportDialog = !state.showImportDialog;
        },
        toggleShowPrivateDeclarations(state) {
            state.showPrivateDeclarations = !state.showPrivateDeclarations;
        },
    },
});

const { actions, reducer } = packageDataSlice;
export const {
    toggleIsExpanded: toggleIsExpandedInTreeView,
    setScrollOffset: setTreeViewScrollOffset,
    toggleImportDialog: togglePackageDataImportDialog,
    toggleShowPrivateDeclarations,
} = actions;
export default reducer;

const selectPackageData = (state: RootState) => state.packageData;
export const selectIsExpandedInTreeView =
    (target: string) =>
    (state: RootState): boolean =>
        Boolean(selectPackageData(state).expandedInTreeView[target]);
export const selectAllExpandedInTreeView = (
    state: RootState,
): { [target: string]: true } => selectPackageData(state).expandedInTreeView;
export const selectTreeViewScrollOffset = (state: RootState): number =>
    selectPackageData(state).treeViewScrollOffset;
export const selectShowPackageDataImportDialog = (state: RootState): boolean =>
    selectPackageData(state).showImportDialog;
export const selectShowPrivateDeclarations = (state: RootState): boolean =>
    selectPackageData(state).showPrivateDeclarations;