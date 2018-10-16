import React from 'react';
import DropArea from '../../general/droparea/Component';

class TemplateSerialRunTab extends React.Component {

    runSerialTemplate = file => {
        const formData = new FormData();
        formData.append('file', file);
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