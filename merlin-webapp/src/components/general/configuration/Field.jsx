import React from 'react';
import './style.css';
import ConfigurationTextField from './TextField';
import ConfigurationCheckboxGroup from './CheckboxGroup';
import ConfigurationFieldLabel from './FieldLabel';

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

        if (value && this.props.updateValue) {
            this.props.updateValue(value);
        }
    };

    render = () => {

        let input;

        switch (this.props.type) {
            case 'checkbox-group':
                console.log();
                input = <ConfigurationCheckboxGroup
                    value={this.props.value}
                />;
                break;
            default:
                input = <ConfigurationTextField
                    editing={this.state.editing}
                    type={this.props.type}
                    value={this.props.value}
                    title={this.props.title}
                    startEditing={this.startEditing}
                    stopEditing={this.stopEditing}
                />;
        }

        return <div className={'configuration-field'}>
            {this.props.title ?
                <ConfigurationFieldLabel title={this.props.title}/>
                : undefined}
            <div className={'configuration-field-value-container'}>
                {input}
            </div>
        </div>;
    }
}

export default ConfigurationField;
