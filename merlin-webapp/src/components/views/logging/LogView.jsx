import React from 'react';
import {connect} from 'react-redux';
import {PageHeader} from '../../general/BootstrapComponents';
import LogFilters from './LogFilters';
import {changeFilter} from '../../../actions';

function LogView(props) {
    return (
        <div>
            <PageHeader>Log viewer</PageHeader>
            <LogFilters
                filters={props.filters}
                changeFilter={props.changeFilter}
            />
        </div>
    );
}

const mapStateToProps = state => ({
    filters: state.log.filters
});

const actions = {
    changeFilter
};

export default connect(mapStateToProps, actions)(LogView);