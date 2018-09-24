import React from 'react';
import ConfigurationEditingField from './EditingField';
import './style.css';

class ConfigurationField extends React.Component {

    state = {
        editing: false
    };

    constructor(props) {
        super(props);

        this.startEditing = this.startEditing.bind(this);
        this.stopEditing = this.stopEditing.bind(this);
    }

    startEditing = () => {
        this.setState({
            editing: true
        });
    };

    stopEditing = value => {
        this.setState({
            editing: false
        });

        if (value) {
            this.props.updateValue(value);
        }
    };

    render = () => <div className={'configuration-field'}>
        <div className={'configuration-field-label'}>{this.props.title}</div>
        <div className={'configuration-field-value-container'}>
            {this.state.editing ?
                <ConfigurationEditingField
                    type={this.props.type}
                    value={this.props.value}
                    name={this.props.title}
                    stopEditing={this.stopEditing}
                /> :
                <span
                    onClick={this.startEditing}
                    className={'configuration-field-value'}
                >
                    {this.props.value}
                </span>
            }
        </div>
    </div>;
}

export default ConfigurationField;
