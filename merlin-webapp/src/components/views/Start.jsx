import React from 'react';
import {PageHeader} from '../general/BootstrapComponents';
import {isDevelopmentMode} from "../../utilities/global";

class Start extends React.Component {

    render() {
        let todo = '';
        if (isDevelopmentMode()) {
            todo = <code><h3>ToDo</h3>
                <ul>
                    <li>Use Webpack for handling dev and production mode.</li>
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
                        <li>Aktualisierung von Template-variablen in der Template-Run-Seite funktioniert noch nicht
                            immer automatisch.
                        </li>
                        <li>Aktualisierung der Log-Meldungen im Log-Viewer (bzw. Refresh-Button) funktioniert noch nicht
                        zuverlässig.</li>
                        <li>Serienbrieffunktion ist noch nicht freigeschaltet.</li>
                        <li>Die gebundelte Java-Version kann abstürzen. Dann bitte einfach Merlin-App neu starten und mir mitteilen.</li>
                    </ul>
                    Viel Spaß!
                </code>
            </React.Fragment>
        );
    }
}

export default Start;
