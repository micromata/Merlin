import React from 'react';
import {Link} from 'react-router-dom';
import {LinkContainer} from 'react-router-bootstrap';
import {Nav, Navbar, NavItem} from 'react-bootstrap';

class Menu extends React.Component {

    render() {

        return (
            <Navbar collapseOnSelect>
                <Navbar.Header>
                    <Navbar.Brand>
                        <Link to={'/'}>
                            Merlin Webapp
                        </Link>
                    </Navbar.Brand>
                    <Navbar.Toggle/>
                </Navbar.Header>
                <Navbar.Collapse>
                    <Nav>
                        {
                            this.props.routes.map((route, index) => (
                                <LinkContainer
                                    key={index}
                                    to={route[1]}
                                    exact
                                >
                                    <NavItem
                                        eventKey={index + 1}
                                    >
                                        {route[0]}
                                    </NavItem>
                                </LinkContainer>
                            ))
                        }
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        );
    }
}

export default Menu;
