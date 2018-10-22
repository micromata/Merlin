import React from 'react';
import {Link} from 'react-router-dom';
import './style.css'
import {formatDateTime} from '../../../utilities/global';

function Footer({versionInfo}) {
    return <div className={'footer'}>
        <p className={'version'}>
            Version {versionInfo.version} * Build Date {formatDateTime(versionInfo.buildDate)}
            {versionInfo.updateVersion ? <span> * <Link to={'/update'}>New Version Available</Link></span> : ''}
        </p>
    </div>;
}

export default Footer;