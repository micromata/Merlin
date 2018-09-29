import React from 'react';
import {PageHeader} from 'react-bootstrap';

class Start extends React.Component {

    render() {
        return (
            <div>
                <PageHeader>
                    Start
                </PageHeader>

                <h3>ToDo</h3>
                <ul>
                    <li>I18n: Get the translations from the server via json for labels etc. Do it globally or on every single view?</li>
                </ul>

            </div>
        );
    }
}

export default Start;
