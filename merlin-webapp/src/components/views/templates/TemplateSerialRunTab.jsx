import React from 'react';

class TemplateSerialRunTab extends React.Component {

    runSerialTemplate = file => {
        const formData = new FormData();
        formData.append('file', file);
    };

    render() {
        return (
            <React.Fragment>
                <h4>Generation from a file:</h4>
                Not yet implemented.
                {/*<DropArea upload={this.runSerialTemplate} />*/}
            </React.Fragment>
        );
    }
}

export default TemplateSerialRunTab;