import React from 'react';
import {Button} from 'reactstrap';
import {getRestServiceUrl} from "../../utilities/global";

/*
* Link to a file. If Merlin is running both local, the server and the client, the file will be opened (e. g. in Excel).
* For remote clients the desired file will be downloaded.
 */
class LinkFile extends React.Component {
    openFile() {
        fetch(getRestServiceUrl('files/open-local-file', {primaryKey: this.props.primaryKey}), {
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