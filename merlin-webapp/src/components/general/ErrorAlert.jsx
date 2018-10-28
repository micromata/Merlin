import React from 'react';
import {Alert, Button} from 'reactstrap';

const ErrorAlert = (props) => <Alert
    color={'danger'}
>
    <h4>{props.title}</h4>
    <p>{props.description}</p>
    {props.action ? <p>
        <Button
            bsStyle={props.action.style}
            onClick={props.action.handleClick}
        >
            {props.action.title}
        </Button>
    </p> : undefined}
</Alert>;

export default ErrorAlert;
