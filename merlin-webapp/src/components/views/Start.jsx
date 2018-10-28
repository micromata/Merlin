import React from 'react';
import {PageHeader} from '../general/BootstrapComponents';
import {isDevelopmentMode} from '../../utilities/global';

class Start extends React.Component {

    render() {
        let todo = '';
        if (isDevelopmentMode()) {
            todo = <code><h3>ToDo</h3>
                <ul>
                    <li>I18n: Get the translations from the server via json for labels etc. Do it globally or on every
                        single view?
                    </li>
                </ul>
            </code>
        }
        return (
            <React.Fragment>
                <PageHeader>
                    Start
                </PageHeader>
                {todo}
                <code><h3>Known issues</h3>
                    <ul>
                        <li>Fehler beim Ausführen von Templates darstellen (z. B. unbekannte Steuerelemente).</li>
                        <li>Alles noch runder machen...</li>
                    </ul>
                    Viel Spaß!
                </code>
            </React.Fragment>
        );
    }
}

export default Start;
