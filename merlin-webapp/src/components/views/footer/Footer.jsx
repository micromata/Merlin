import React from 'react';
import {Link} from 'react-router-dom';
import './style.css'
import {formatDateTime} from '../../../utilities/global';
import I18n from "../../general/translation/I18n";

function Footer({versionInfo}) {

    if (versionInfo.failed) {
        return (
            <div className={'footer'}>
                <p className={'version'}>Cannot fetch version information.</p>
            </div>
        );
    }

    return <div className={'footer'}>
        <p className={'version'}>
            <I18n name={'version'}/> {versionInfo.version} * <I18n name={'version.buildDate'}/> {formatDateTime(versionInfo.buildDate)}
            {versionInfo.updateVersion ? <span> * <Link to={'/update'}><I18n name={'version.newVersionAvailable'}/></Link></span> : ''}
        </p>
    </div>;
}

export default Footer;
