import React from 'react';
import {Button} from 'reactstrap';
import {IconDownload} from '../../general/IconComponents';
import {getResponseHeaderFilename, getRestServiceUrl} from '../../../utilities/global';
import DropArea from '../../general/droparea/DropArea';
import downloadFile from '../../../utilities/download';
import {uploadFile} from '../../../actions';
import connect from 'react-redux/es/connect/connect';

class TemplateSerialRunTab extends React.Component {

    getSerialTemplate = () => {
        let filename;
        fetch(getRestServiceUrl('templates/get-serial-run-excel', {
            templatePrimaryKey: this.props.templatePrimaryKey,
            templateDefinitionPrimaryKey: this.props.templateDefinitionPrimaryKey}), {
            method: 'GET',
            headers: {
                "Content-Type": "text/plain; charset=utf-8"
            }
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
                <Button className={'btn-outline-primary'} onClick={this.getSerialTemplate}>
                    <IconDownload/> Excel serial template
                </Button>
                <h4>Upload serial data Excel file:</h4>
                <DropArea upload={this.props.uploadFile} />
            </React.Fragment>
        );
    }
}

const mapStateToProps = () => ({});

const actions = {
    uploadFile
};

export default connect(mapStateToProps, actions)(TemplateSerialRunTab);
