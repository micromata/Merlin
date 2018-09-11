import React from 'react';
import {PageHeader} from 'react-bootstrap';
import {connect} from 'react-redux';
import ConfigField from './Field';
import {loadConfig} from '../../../actions/';

class View extends React.Component {

    render() {
        return (
            <div>
                <PageHeader>Config</PageHeader>

                {
                    Object.keys(this.props.config).map(key =>
                        <ConfigField
                            key={key}
                            title={key}
                            value={this.props.config[key]}
                        />
                    )
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
    loadConfig
};

export default connect(mapStateToProps, actions)(View);
