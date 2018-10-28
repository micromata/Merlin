import React from 'react';
import PropTypes from 'prop-types';
import {FormButton, FormInput, FormLabel, FormSelect, FormOption} from '../../general/forms/FormComponents';
import {IconRefresh} from '../../general/IconComponents';
import I18n from '../../general/translation/I18n';

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
                hint={<I18n name={'logviewer.filter.level.hint'} />}
            >
                <FormOption value={'error'} />
                <FormOption value={'warn'} />
                <FormOption value={'info'} />
                <FormOption value={'debug'} />
                <FormOption value={'trace'} />
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
                hint={<I18n name={'logviewer.filter.location.hint'} />}
            >
                <FormOption value={'none'} i18nKey={'logviewer.filter.location.option.none'} />
                <FormOption value={'short'} i18nKey={'logviewer.filter.location.option.short'} />
                <FormOption value={'normal'} i18nKey={'logviewer.filter.location.option.normal'} />
            </FormSelect>

            <FormSelect
                value={filters.showStackTrace}
                name={'showStackTrace'}
                onChange={changeFilter}
                hint={'Show/hide stack traces.'}
            >
                <option value={'false'}>none</option>
                <option value={'true'}>stacktraces</option>
            </FormSelect>

            <FormSelect
                value={filters.maxSize}
                name={'maxSize'}
                onChange={changeFilter}
                hint={'Limits result size.'}
            >
                <option>50</option>
                <option>100</option>
                <option>500</option>
                <option>1000</option>
                <option>10000</option>
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
