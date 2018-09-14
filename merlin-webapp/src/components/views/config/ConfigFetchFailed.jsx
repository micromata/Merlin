import React from 'react';
import {Alert, Button} from 'react-bootstrap';

class ConfigFetchFailed extends React.Component {

    render() {
        return (
            <Alert
                bsStyle={'danger'}
            >
                <h4>Oh snap! The config couldn't be loaded!</h4>
                <p>
                    We don't know what happened and now we cannot display the config. The only thing we can do, is
                    offering this cool 'Try Again!' Button. Come on hit it.
                </p>
                <p>
                    <Button onClick={this.props.fetchConfig}>
                        Try again!
                    </Button>
                </p>
            </Alert>
        );
    }
}

export default ConfigFetchFailed;
