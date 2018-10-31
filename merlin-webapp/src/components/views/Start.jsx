import React from 'react';
import {PageHeader} from '../general/BootstrapComponents';

class Start extends React.Component {

    render() {
        let todo = '';
        return (
            <React.Fragment>
                <PageHeader>
                    Start
                </PageHeader>
                {todo}
            </React.Fragment>
        );
    }
}

export default Start;
