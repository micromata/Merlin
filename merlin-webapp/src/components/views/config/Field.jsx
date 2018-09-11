import React from 'react';
import './style.css';
import EditingField from './EditingField';

class ConfigField extends React.Component {

    state = {
        editing: false
    };

    constructor(props) {
        super(props);

        this.startEditing = this.startEditing.bind(this);
        this.stopEditing = this.stopEditing.bind(this);
    }

    startEditing() {
        if (this.state.editing) {
            return;
        }

        this.setState({
            editing: true
        });
    }

    stopEditing() {
        if (!this.state.editing) {
            return;
        }

        this.setState({
            editing: false
        });
    }

    render() {
        return (
            <div className={'config-form'}>
                <div className={'config-label'}>{this.props.title}:</div>
                <div
                    className={`config-value ${this.state.editing ? 'editing' : 'not-editing'}`}
                >
                    {
                        this.state.editing ?
                            (<EditingField
                                type={this.props.type}
                                value={this.props.value}
                                name={this.props.title}
                                stopEditing={this.stopEditing}
                            />) :
                            <span
                                onClick={this.startEditing}
                            >{this.props.value}</span>
                    }
                </div>
            </div>
        );
    }
}

export default ConfigField;
