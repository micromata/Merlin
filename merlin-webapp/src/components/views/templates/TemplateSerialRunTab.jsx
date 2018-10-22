import React from 'react';
import {Button} from 'reactstrap';
import {IconDownload} from "../../general/IconComponents";
import {getResponseHeaderFilename, getRestServiceUrl} from "../../../utilities/global";
import downloadFile from "../../../utilities/download";

class TemplateSerialRunTab extends React.Component {

    runSerialTemplate = file => {
        const formData = new FormData();
        formData.append('file', file);
    };

    getSerialTemplate = () => {
        let filename;
        let params = [['templatePrimaryKey', this.props.templatePrimaryKey],
            ['templateDefinitionPrimaryKey', this.props.templateDefinitionPrimaryKey]];
        fetch(getRestServiceUrl('/templates/get-serial-run-excel', params), {
            method: 'GET',
            headers,
            body: body
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }

                filename = getResponseHeaderFilename(response.headers.get('Content-Disposition'));
                return response.blob();
            })
            .then(blob => downloadFile(blob, filename))
            .catch(alert);
    };

    render() {
        return (
            <React.Fragment>
                <h4>Generation from a file:</h4>
                <a href={this.getSerialTemplate()}><Button className={'btn-outline-primary'}>
                    <IconDownload/> Excel serial template
                </Button></a>
                {/*<DropArea upload={this.runSerialTemplate} />*/}
            </React.Fragment>
        );
    }
}

export default TemplateSerialRunTab;