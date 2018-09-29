import React from 'react';
import ConfigurationEditingField from './EditingField';

const ConfigurationTextField = (props) => <div>
    {props.editing ?
        <ConfigurationEditingField
            type={props.type}
            value={props.value}
            name={props.title}
            stopEditing={props.stopEditing}
        /> :
        <span
            onClick={props.startEditing}
            className={'configuration-field-value'}
        >
            {props.value}
        </span>
    }
</div>;

export default ConfigurationTextField;
