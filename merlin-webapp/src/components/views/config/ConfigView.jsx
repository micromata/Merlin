import React from 'react';
import {PageHeader} from 'react-bootstrap';
import {connect} from 'react-redux';
import ConfigField from './Field';
import {fetchConfig, fetchConfigIfNeeded} from '../../../actions/';
import ConfigFetchFailed from './ConfigFetchFailed';

const ConfigView = (props) => {

    props.fetchConfigIfNeeded();

    return (
        <div>
            <PageHeader>Config</PageHeader>

            {
                props.config.failed ?
                    <ConfigFetchFailed
                        fetchConfig={props.fetchConfig}
                    /> :
                    (props.config.loaded ? Object.keys(props.config.properties).map(key =>
                        <ConfigField
                            key={key}
                            title={key}
                            value={props.config.properties[key]}
                        />
                    ) : <i>Loading...</i>)
            }
        </div>
    );
};

const mapStateToProps = state => ({
        config: state.config
});

const actions = {
    fetchConfig,
    fetchConfigIfNeeded
};

export default connect(mapStateToProps, actions)(ConfigView);
