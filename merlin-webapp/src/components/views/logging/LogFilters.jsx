import React from 'react';
import PropTypes from 'prop-types';
import {FormButton, FormInput, FormLabel, FormSelect} from '../../general/forms/FormComponents';
import {IconRefresh} from '../../general/IconComponents';

function LogFilters({loadLog, changeFilter, filters}) {

    return (
        <form
            onSubmit={loadLog}
            className={'form-inline'}
        >
            <FormLabel length={1}>
                Filter:
            </FormLabel>

            <FormSelect
                value={filters.threshold}
                name={'threshold'}
                onChange={changeFilter}
            >
                <option>error</option>
                <option>warn</option>
                <option>info</option>
                <option>debug</option>
                <option>trace</option>
            </FormSelect>

            <FormInput
                value={filters.search}
                name={'search'}
                onChange={changeFilter}
                fieldLength={5}
            />

            <FormSelect
                value={filters.locationFormat}
                name={'locationFormat'}
                onChange={changeFilter}
            >
                <option>none</option>
                <option>short</option>
                <option>normal</option>
            </FormSelect>

            <FormSelect
                value={filters.showStackTrace}
                name={'showStackTrace'}
                onChange={changeFilter}
            >
                <option value={'false'}>no stacktraces</option>
                <option value={'true'}>show stacktraces</option>
            </FormSelect>

            <FormSelect
                value={filters.maxSize}
                name={'maxSize'}
                onChange={changeFilter}
            >
                <option>50</option>
                <option>100</option>
                <option>500</option>
                <option>1000</option>
                <option>10000</option>
            </FormSelect>

            <FormSelect
                value={filters.ascendingOrder}
                name={'ascendingOrder'}
                onChange={changeFilter}
            >
                <option value={'true'}>ascending</option>
                <option value={'false'}>descending</option>
            </FormSelect>

            <FormButton type={'submit'} bsStyle={'primary'}>
                <IconRefresh />
            </FormButton>
        </form>
    );
}

LogFilters.propTypes = {
    changeFilter: PropTypes.func.isRequired,
    filters: PropTypes.shape({
        threshold: PropTypes.oneOf(['error', 'warn', 'info', 'debug', 'trace']),
        search: PropTypes.string,
        locationFormat: PropTypes.oneOf(['none', 'short', 'normal']),
        showStackTrace: PropTypes.oneOf(['true', 'false']),
        maxSize: PropTypes.oneOf(['50', '100', '500', '1000', '10000']),
        ascendingOrder: PropTypes.oneOf(['true', 'false'])
    }).isRequired,
    loadLog: PropTypes.func.isRequired
};

export default LogFilters;
