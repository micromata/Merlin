import React from 'react';
import {PageHeader} from 'react-bootstrap';
import {connect} from 'react-redux';
import ConfigField from './Field';
import {fetchConfigIfNeeded} from '../../../actions/';

class View extends React.Component {

    render() {

        this.props.fetchConfig();

        return (
            <div>
                <PageHeader>Config</PageHeader>

                {
                    this.props.config.loaded ? Object.keys(this.props.config.properties).map(key =>
                        <ConfigField
                            key={key}
                            title={key}
                            value={this.props.config.properties[key]}
                        />
                    ) : <i>Loading...</i>
                }
            </div>
        );
    }
}

const mapStateToProps = state => {
    return {
        config: state.config
    };
};

const actions = {
    fetchConfig: fetchConfigIfNeeded
};

export default connect(mapStateToProps, actions)(View);
