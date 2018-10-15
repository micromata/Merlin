import React from 'react';
import {getRestServiceUrl} from '../../../utilities/global';
import DropArea from '../../general/droparea/Component';

class TemplateSerialRunTab extends React.Component {

    runSerialTemplate = file => {
        const formData = new FormData();
        formData.append('file', file);

        this.runTemplate(getRestServiceUrl('files/upload'), undefined, formData);
    };

    render() {
        return (
            <React.Fragment>
                <h4>Generation from a file:</h4>
                <DropArea upload={this.runSerialTemplate} />
                <br />
            </React.Fragment>
        );
    }
}

export default TemplateSerialRunTab;