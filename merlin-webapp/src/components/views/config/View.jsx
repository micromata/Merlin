import React from 'react';
import {PageHeader} from 'react-bootstrap';
import ConfigField from "./Field";

class View extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            value: {}
        };
    }

    render() {
        return (
            <div>
                <PageHeader>Config</PageHeader>

                <ConfigField
                    title={'Port'}
                    type={'number'}
                    value={8080}
                />
                <ConfigField
                    title={'Path'}
                    type={'text'}
                    value={'~/test'}
                />
            </div>
        );
    }
}

export default View;
