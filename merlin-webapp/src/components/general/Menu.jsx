import React from 'react';
import {NavLink as ReactRouterNavLink} from 'react-router-dom';
import {Collapse, Nav, Navbar, NavbarBrand, NavbarToggler, NavItem, NavLink, UncontrolledTooltip} from 'reactstrap';
import DropArea from "./droparea/DropArea";
import {getResponseHeaderFilename, getRestServiceUrl} from "../../utilities/global";
import downloadFile from "../../utilities/download";
import LoadingOverlay from "./loading/LoadingOverlay";
import I18n from "./translation/I18n";

class Menu extends React.Component {
    getNavElement = (route, index) => {
        if (index === 0) {
            return '';
        }
        let addition = '';
        let className = '';
        // Additional Route Settings
        if (route.length >= 4) {
            if (route[3].badge) {
                addition = route[3].badge;
            }

            if (route[3].className) {
                className = route[3].className;
            }
        }
        return (
            <NavItem key={index}>
                <NavLink
                    to={route[1]}
                    tag={ReactRouterNavLink}
                    className={className}
                >
                    {route[0]} {addition}
                </NavLink>
            </NavItem>
        );
    };

    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.state = {
            loading: false,
            isOpen: false
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
                this.setState({loading: false});
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(text)
                    });
                }
                filename = getResponseHeaderFilename(response.headers.get('Content-Disposition'));
                return response.blob();
            })
            .then(blob => downloadFile(blob, filename))
            .catch(alert);
    }

    toggle() {
        this.setState({
            isOpen: !this.state.isOpen
        });
    }

    render() {
        return (
            <Navbar className={'fixed-top'} color="light" light expand="md">
                <NavbarBrand to="/" tag={ReactRouterNavLink}><img alt={'Merlin logo'}
                                                                  src={'../../../images/merlin-icon.png'}
                                                                  width={'50px'}/>Merlin Runner</NavbarBrand>
                <NavbarToggler onClick={this.toggle}/>
                <Collapse isOpen={this.state.isOpen} navbar>
                    <Nav className="ml-auto" navbar>
                        {
                            this.props.routes.map((route, index) => (
                                this.getNavElement(route, index)
                            ))
                        }
                    </Nav>
                    <DropArea id={'menuDropZone'} className={'menu'}
                              upload={this.uploadFile}
                    />
                    <UncontrolledTooltip placement={'left'} target={'menuDropZone'}>
                       <I18n name={'common.droparea.hint'}/>
                    </UncontrolledTooltip> </Collapse>
                {this.state.loading ? <LoadingOverlay/> : ''}
            </Navbar>
        );
    }

    /* <UncontrolledDropdown nav inNavbar>
         <DropdownToggle nav caret>
             Options
         </DropdownToggle>
         <DropdownMenu right>
             <DropdownItem>
                 Option 1
             </DropdownItem>
             <DropdownItem>
                 Option 2
             </DropdownItem>
             <DropdownItem divider />
             <DropdownItem>
                 Reset
             </DropdownItem>
         </DropdownMenu>
     </UncontrolledDropdown>*/
}

export default Menu;
