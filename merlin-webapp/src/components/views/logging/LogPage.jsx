import React from 'react';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {PageHeader} from '../../general/BootstrapComponents';
import LogFilters from './LogFilters';
import {changeFilter, requestLogReload} from '../../../actions';
import LogTable from './LogTable';
import I18n from '../../general/translation/I18n';
import './LogViewer.css';

class LogPage extends React.Component {

    componentDidMount = () => this.props.loadLog();

    handleToggleSortOrder = () => {
        let filters = this.props.filters;
        this.props.changeFilter('ascendingOrder', filters.ascendingOrder === 'true' ? 'false'  : 'true');
        this.props.loadLog();
    };

    handleInputChange = (event) => {
        this.props.changeFilter(event.target.name, event.target.value);
    };



    render = () => (
            <React.Fragment>
                <PageHeader><I18n name={'logviewer'} /></PageHeader>
                <LogFilters
                    filters={this.props.filters}
                    changeFilter={this.handleInputChange}
                    loadLog={(event) => {
                        event.preventDefault();
                        this.props.loadLog();
                    }}
                />
                <LogTable
                    search={this.props.filters.search}
                    locationFormat={this.props.filters.locationFormat}
                    entries={this.props.entries}
                    ascendingOrder={this.props.filters.ascendingOrder}
                    toggleOrder={this.handleToggleSortOrder}
                    showStackTrace={this.props.filters.showStackTrace}
                />
            </React.Fragment>
        );
}

LogPage.propTypes = {
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

const mapStateToProps = state => state.log;

const actions = {
    changeFilter,
    loadLog: requestLogReload
};

export default connect(mapStateToProps, actions)(LogPage);