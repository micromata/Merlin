import React from 'react';
import {PageHeader} from '../general/BootstrapComponents';
import I18n from "../general/translation/I18n";

class Start extends React.Component {
    render() {
        return (
            <React.Fragment>
                <PageHeader>
                    <I18n name={'startscreen.welcome.title'}/>
                </PageHeader>
                <div className="welcome-intro"><I18n name={'startscreen.welcome.text'}/></div>
                <div className="welcome-enjoy"><I18n name={'startscreen.welcome.enjoy'}/></div>
                <div className="welcome-documentation-link"><a className={'btn btn-link btn-outline-primary'} href={'/docs/index.html'} target="_blank" rel="noopener noreferrer"><I18n name={'startscreen.welcome.documentation'}/></a></div>
            </React.Fragment>
        );
    }
}

export default Start;
