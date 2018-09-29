import React from 'react';
import {ButtonToolbar, ToggleButton, ToggleButtonGroup} from 'react-bootstrap';

const ConfigurationCheckboxGroup = (props) => <div>
    <ButtonToolbar>
        <ToggleButtonGroup
            type={'checkbox'}
            defaultValue={Object.keys(props.value).filter(item => props.value[item])}
        >
            {Object.keys(props.value).map(item => <ToggleButton
                    key={item}
                    value={item}>
                    {item}
                </ToggleButton>
            )}
        </ToggleButtonGroup>
    </ButtonToolbar>
</div>;

export default ConfigurationCheckboxGroup;
