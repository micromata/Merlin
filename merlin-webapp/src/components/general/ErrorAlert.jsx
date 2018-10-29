import React from 'react';
import {Alert, Button} from 'reactstrap';
import I18n from "./translation/I18n";

const ErrorAlert = (props) => <Alert
    color={'danger'}
>
    <h4>{props.titleKey ? <I18n name={props.titleKey}/> : props.title}</h4>
    <p>{props.descriptionKey ? <I18n name={props.descriptionKey}/> :props.description}</p>
    {props.action ? <p>
        <Button
            bsStyle={props.action.style}
            onClick={props.action.handleClick}
        >
            {props.action.titleKey ? <I18n name={props.action.titleKey}/> : props.action.title}
        </Button>
    </p> : undefined}
</Alert>;

export default ErrorAlert;
