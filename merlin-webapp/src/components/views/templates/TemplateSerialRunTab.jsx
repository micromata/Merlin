import React from 'react';
import {Button} from 'reactstrap';
import {IconDownload} from '../../general/IconComponents';
import {getResponseHeaderFilename, getRestServiceUrl} from '../../../utilities/global';
import DropArea from '../../general/droparea/DropArea';
import downloadFile from '../../../utilities/download';
import {uploadFile} from '../../../actions';
import connect from 'react-redux/es/connect/connect';
import LoadingOverlay from '../../general/loading/LoadingOverlay';
import FailedOverlay from '../../general/loading/failed/Overlay';

class TemplateSerialRunTab extends React.Component {
    getSerialTemplate = () => {
        let filename;

        this.setState({
            loading: true
        });

        fetch(getRestServiceUrl('templates/get-serial-run-excel', {
            templatePrimaryKey: this.props.templatePrimaryKey,
            templateDefinitionPrimaryKey: this.props.templateDefinitionPrimaryKey
        }), {
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
            .then(blob => {
                this.setState({
                    loading: false
                });
                downloadFile(blob, filename);
            })
            .catch(error => {
                this.setState({
                    loading: false,
                    generationFailed: error.toString()
                });
            });
    };

    constructor(props) {
        super(props);
        this.state = {
            loading: false,
            generationFailed: false,
            runFailed: false
        };
        this.uploadFile = this.uploadFile.bind(this);
    }

    uploadFile(file) {
        this.setState({loading: true});
        const formData = new FormData();
        formData.append('file', file);
        let filename;
        return fetch(getRestServiceUrl('files/upload'), {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(text)
                    });
                }
                filename = getResponseHeaderFilename(response.headers.get('Content-Disposition'));
                return response.blob();
            })
            .then(blob => {
                this.setState({loading: false});
                downloadFile(blob, filename);
            })
            .catch(error => {
                this.setState({
                    loading: false,
                    runFailed: error.toString()
                });
            });
    }

    render() {
        return (
            <React.Fragment>
                <h4>Generation from a file:</h4>
                <Button className={'btn-outline-primary'} onClick={this.getSerialTemplate}>
                    <IconDownload/> Excel serial template
                </Button>
                <h4>Upload serial data Excel file:</h4>
                <DropArea upload={this.uploadFile}><b> Select a file,</b> or drop one here.</DropArea>
                {this.state.loading ? <LoadingOverlay/> : ''}
                {this.state.generationFailed ? <FailedOverlay
                    title={'Template Serial Generation failed'}
                    text={this.state.generationFailed}
                    closeModal={() => this.setState({generationFailed: false})}
                /> : ''}
                {this.state.runFailed ? <FailedOverlay
                    title={'Template Serial Run failed'}
                    text={this.state.runFailed}
                    closeModal={() => this.setState({runFailed: false})}
                /> : ''}
            </React.Fragment>
        );
    }
}

const mapStateToProps = () => ({});

const actions = {
    uploadFile
};

export default connect(mapStateToProps, actions)(TemplateSerialRunTab);
