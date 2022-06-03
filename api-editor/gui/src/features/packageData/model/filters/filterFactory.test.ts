import { createFilterFromString } from './filterFactory';
import { ConjunctiveFilter } from './ConjunctiveFilter';
import VisibilityFilter, { Visibility } from './VisibilityFilter';
import { NegatedFilter } from './NegatedFilter';
import NameFilter from "./NameFilter";
import UsageFilter from "./UsageFilter";
import UsefulnessFilter from "./UsefulnessFilter";
import {greaterThan} from "./comparisons";

describe('createFilterFromString', () => {
    test('handles an empty string', () => {
        const completeFilter = createFilterFromString('');
        expect(completeFilter).toBeInstanceOf(ConjunctiveFilter);
        expect((completeFilter as ConjunctiveFilter).filters).toEqual([]);
    });

    test('handles a single positive token', () => {
        const completeFilter = createFilterFromString('is:public');
        expect(completeFilter).toBeInstanceOf(ConjunctiveFilter);
        expect((completeFilter as ConjunctiveFilter).filters).toHaveLength(1);

        const positiveFilter = (completeFilter as ConjunctiveFilter).filters[0];
        expect(positiveFilter).toBeInstanceOf(VisibilityFilter);
        expect((positiveFilter as VisibilityFilter).visibility).toEqual(Visibility.Public);
    });

    test('handles a single negated token', () => {
        const completeFilter = createFilterFromString('!is:public');
        expect(completeFilter).toBeInstanceOf(ConjunctiveFilter);
        expect((completeFilter as ConjunctiveFilter).filters).toHaveLength(1);

        const negatedFilter = (completeFilter as ConjunctiveFilter).filters[0];
        expect(negatedFilter).toBeInstanceOf(NegatedFilter);

        const positiveFilter = (negatedFilter as NegatedFilter).filter;
        expect(positiveFilter).toBeInstanceOf(VisibilityFilter);
        expect((positiveFilter as VisibilityFilter).visibility).toEqual(Visibility.Public);
    });

    test('handles multiple tokens', () => {
        const completeFilter = createFilterFromString('is:public !is:public');
        expect(completeFilter).toBeInstanceOf(ConjunctiveFilter);
        expect((completeFilter as ConjunctiveFilter).filters).toHaveLength(2);

        // First token
        const positiveFilter1 = (completeFilter as ConjunctiveFilter).filters[0];
        expect(positiveFilter1).toBeInstanceOf(VisibilityFilter);
        expect((positiveFilter1 as VisibilityFilter).visibility).toEqual(Visibility.Public);

        // Second token
        const negatedFilter2 = (completeFilter as ConjunctiveFilter).filters[1];
        expect(negatedFilter2).toBeInstanceOf(NegatedFilter);

        const positiveFilter2 = (negatedFilter2 as NegatedFilter).filter;
        expect(positiveFilter2).toBeInstanceOf(VisibilityFilter);
        expect((positiveFilter2 as VisibilityFilter).visibility).toEqual(Visibility.Public);
    });

    test('handles name filter', () => {
        const completeFilter = createFilterFromString('name:foo');
        expect(completeFilter).toBeInstanceOf(ConjunctiveFilter);
        expect((completeFilter as ConjunctiveFilter).filters).toHaveLength(1);

        const positiveFilter = (completeFilter as ConjunctiveFilter).filters[0];
        expect(positiveFilter).toBeInstanceOf(NameFilter);
        expect((positiveFilter as NameFilter).substring).toEqual('foo');
    })

    test('handles usages filter', () => {
        const completeFilter = createFilterFromString('usages:>2');
        expect(completeFilter).toBeInstanceOf(ConjunctiveFilter);
        expect((completeFilter as ConjunctiveFilter).filters).toHaveLength(1);

        const positiveFilter = (completeFilter as ConjunctiveFilter).filters[0];
        expect(positiveFilter).toBeInstanceOf(UsageFilter);
        expect((positiveFilter as UsageFilter).comparison).toEqual(greaterThan);
        expect((positiveFilter as UsageFilter).expectedUsage).toEqual(2);
    })

    test('handles usefulness filter', () => {
        const completeFilter = createFilterFromString('usefulness:>2');
        expect(completeFilter).toBeInstanceOf(ConjunctiveFilter);
        expect((completeFilter as ConjunctiveFilter).filters).toHaveLength(1);

        const positiveFilter = (completeFilter as ConjunctiveFilter).filters[0];
        expect(positiveFilter).toBeInstanceOf(UsefulnessFilter);
        expect((positiveFilter as UsefulnessFilter).comparison).toEqual(greaterThan);
        expect((positiveFilter as UsefulnessFilter).expectedUsefulness).toEqual(2);
    })
});
