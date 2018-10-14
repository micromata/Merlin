import React from 'react';
import {Button} from 'reactstrap';
import {getResponseHeaderFilename, getRestServiceUrl} from "../../actions/global";
import downloadFile from "../../utilities/download";

/*
* Link to a file. If Merlin is running both local, the server and the client, the file will be opened (e. g. in Excel).
* For remote clients the desired file will be downloaded.
 */
class LinkFile extends React.Component {
    openFile() {
        let filename;
        fetch(getRestServiceUrl('files/open-local-file', {filepath: this.props.filepath}), {
            method: "GET",
            dataType: "text",
            headers: {
                "Content-Type": "text/plain; charset=utf-8"
            }
        })
            .then(response => response.text())
            .then((text) => {
                if (text !== 'OK') {
                    alert(text);
                }
            })
            .catch((error) => {
                alert("Can't open file on local file system :-(")
            })
    }

    render() {
        const service = this.props.service;
        const params = this.props.params;
        var url;
        if (params) {
            if (service === 'files/browse-local-filesystem') {
                url = getRestServiceUrl(service) + '?' + params;
            } else {
                url = getRestServiceUrl(service) + '?prettyPrinter=true&' + params;
            }
        } else {
            url = getRestServiceUrl(service) + '?prettyPrinter=true';
        }
        return (
            <Button color="link" onClick={() => this.openFile()}>{this.props.filepath}</Button>
        )
    }

    constructor(props) {
        super(props);
        this.openFile = this.openFile.bind(this);
    }
}

export default LinkFile;